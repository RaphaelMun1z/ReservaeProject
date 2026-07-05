package inventory_service.services;

import inventory_service.messaging.event.InventoryReservationResultEvent;
import inventory_service.messaging.event.OrderReservationItemEvent;
import inventory_service.messaging.event.OrderReservationRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationRequestHandlerService {
    private static final Logger logger =
        LoggerFactory.getLogger(ReservationRequestHandlerService.class);

    private final InventoryManagementService inventoryManagementService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ReservationRequestHandlerService(
        InventoryManagementService inventoryManagementService,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.inventoryManagementService = inventoryManagementService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void handle(OrderReservationRequestedEvent event) {
        List<OrderReservationItemEvent> requestedItems = event.items();

        logger.info(
            "Solicitação de reserva recebida. Pedido: {}. Usuário: {}. Itens: {}.",
            event.orderId(),
            event.userId(),
            requestedItems
        );

        if (requestedItems == null || requestedItems.isEmpty()) {
            publishFailureEvent(
                event.orderId(),
                List.of(),
                "O pedido não possui ingressos para reservar."
            );
            return;
        }

        List<String> reservedReservationIds = new java.util.ArrayList<>();

        try {
            for (OrderReservationItemEvent item : requestedItems) {
                String reservationId = inventoryManagementService.reserveTickets(
                        event.orderId(),
                        event.userId(),
                        item.eventId(),
                        item.sectorId(),
                        item.quantity()
                    )
                    .reservationId();

                reservedReservationIds.add(reservationId);
            }

            publishSuccessEvent(
                event.orderId(),
                reservedReservationIds
            );

            logger.info(
                "Todos os itens do pedido {} foram reservados. Reservas: {}.",
                event.orderId(),
                reservedReservationIds
            );
        } catch (RuntimeException exception) {
            String reason = exception.getMessage();

            if (reason == null || reason.isBlank()) {
                reason = "Não foi possível reservar os ingressos.";
            }

            logger.error(
                "Falha ao reservar os itens do pedido {}. Liberando {} reserva(s). Motivo: {}.",
                event.orderId(),
                reservedReservationIds.size(),
                reason
            );

            releaseReservedReservations(reservedReservationIds);

            publishFailureEvent(
                event.orderId(),
                reservedReservationIds,
                reason
            );
        }
    }

    private void releaseReservedReservations(List<String> reservationIds) {
        for (String reservationId : reservationIds) {
            try {
                inventoryManagementService.releaseReservation(reservationId);
            } catch (RuntimeException exception) {
                logger.error(
                    "Falha ao liberar a reserva {} durante a compensação. Motivo: {}.",
                    reservationId,
                    exception.getMessage()
                );
            }
        }
    }

    private void publishSuccessEvent(
        String orderId,
        List<String> reservationIds
    ) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID().toString(),
                orderId,
                true,
                List.copyOf(reservationIds),
                null,
                LocalDateTime.now()
            );

        applicationEventPublisher.publishEvent(event);
    }

    private void publishFailureEvent(
        String orderId,
        List<String> reservationIds,
        String reason
    ) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID().toString(),
                orderId,
                false,
                List.copyOf(reservationIds),
                reason,
                LocalDateTime.now()
            );

        applicationEventPublisher.publishEvent(event);
    }
}