package inventory_service.services;

import inventory_service.messaging.event.InventoryReservationResultEvent;
import inventory_service.messaging.event.OrderReservationItemEvent;
import inventory_service.messaging.event.OrderReservationRequestedEvent;
import inventory_service.messaging.publisher.InventoryReservationResultPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationRequestHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationRequestHandlerService.class);

    private final InventoryManagementService inventoryManagementService;
    private final InventoryReservationResultPublisher inventoryReservationResultPublisher;

    public ReservationRequestHandlerService(
        InventoryManagementService inventoryManagementService,
        InventoryReservationResultPublisher inventoryReservationResultPublisher
    ) {
        this.inventoryManagementService = inventoryManagementService;
        this.inventoryReservationResultPublisher = inventoryReservationResultPublisher;
    }

    @Transactional
    public void handle(OrderReservationRequestedEvent event) {
        List<OrderReservationItemEvent> requestedItems = event.items();

        logger.info(
            "Solicitação de reserva recebida. Pedido: {}. Evento: {}. Usuário: {}. Itens: {}.",
            event.orderId(),
            event.eventId(),
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

        List<String> createdReservationIds = new ArrayList<>();

        try {
            for (OrderReservationItemEvent item : requestedItems) {
                String reservationId = inventoryManagementService.reserveTickets(
                        event.orderId(),
                        event.userId(),
                        event.eventId(),
                        item.sectorId(),
                        item.quantity()
                    )
                    .reservationId();

                createdReservationIds.add(reservationId);
            }

            publishSuccessEvent(
                event.orderId(),
                createdReservationIds
            );

            logger.info(
                "Todos os itens do pedido {} foram reservados. Reservas: {}.",
                event.orderId(),
                createdReservationIds
            );
        } catch (RuntimeException exception) {
            String reason = exception.getMessage();

            if (reason == null || reason.isBlank()) {
                reason = "Não foi possível reservar os ingressos.";
            }

            logger.error(
                "Falha ao reservar os itens do pedido {}. Liberando {} reserva(s). Motivo: {}.",
                event.orderId(),
                createdReservationIds.size(),
                reason
            );

            releaseCreatedReservations(createdReservationIds);

            publishFailureEvent(
                event.orderId(),
                createdReservationIds,
                reason
            );
        }
    }

    private void releaseCreatedReservations(List<String> reservationIds) {
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

        inventoryReservationResultPublisher.publish(event);
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

        inventoryReservationResultPublisher.publish(event);
    }
}