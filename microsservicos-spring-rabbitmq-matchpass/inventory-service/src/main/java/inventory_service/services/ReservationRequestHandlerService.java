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
import java.util.ArrayList;
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
        List<String> requestedTicketsId = event.items()
            .stream()
            .map(OrderReservationItemEvent::ticketId)
            .toList();

        logger.info(
            "Solicitação de reserva recebida. Pedido: {}. Usuário: {}. Assentos: {}.",
            event.orderId(),
            event.userId(),
            requestedTicketsId
        );

        if (requestedTicketsId.isEmpty()) {
            publishFailureEvent(
                event.orderId(),
                requestedTicketsId,
                "O pedido não possui assentos para reservar."
            );
            return;
        }

        List<String> reservedTicketsId = new ArrayList<>();

        try {
            for (String ticketId : requestedTicketsId) {
                inventoryManagementService.tryReserveTicket(
                    ticketId,
                    event.userId()
                );

                reservedTicketsId.add(ticketId);
            }

            publishSuccessEvent(
                event.orderId(),
                reservedTicketsId
            );

            logger.info(
                "Todos os assentos do pedido {} foram reservados.",
                event.orderId()
            );
        } catch (RuntimeException exception) {
            String reason = exception.getMessage();

            if (reason == null || reason.isBlank()) {
                reason = "Não foi possível reservar os ingressos.";
            }

            logger.error(
                "Falha ao reservar os assentos do pedido {}. Liberando {} assento(s). Motivo: {}.",
                event.orderId(),
                reservedTicketsId.size(),
                reason
            );

            releaseReservedTickets(reservedTicketsId);

            publishFailureEvent(
                event.orderId(),
                requestedTicketsId,
                reason
            );
        }
    }

    private void releaseReservedTickets(List<String> reservedTicketsId) {
        reservedTicketsId.forEach(ticketId -> {
            try {
                inventoryManagementService.releaseTicket(ticketId);
            } catch (RuntimeException exception) {
                logger.error(
                    "Falha ao liberar o assento {} durante a compensação. Motivo: {}.",
                    ticketId,
                    exception.getMessage()
                );
            }
        });
    }

    private void publishSuccessEvent(
        String orderId,
        List<String> ticketsId
    ) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID()
                    .toString(),
                orderId,
                true,
                List.copyOf(ticketsId),
                null,
                LocalDateTime.now()
            );

        applicationEventPublisher.publishEvent(event);
    }

    private void publishFailureEvent(
        String orderId,
        List<String> ticketsId,
        String reason
    ) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID()
                    .toString(),
                orderId,
                false,
                List.copyOf(ticketsId),
                reason,
                LocalDateTime.now()
            );

        applicationEventPublisher.publishEvent(event);
    }
}