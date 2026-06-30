package inventory_service.messaging.consumer;

import inventory_service.messaging.event.OrderReservationItemEvent;
import inventory_service.messaging.event.OrderReservationRequestedEvent;
import inventory_service.messaging.publisher.InventoryReservationResultPublisher;
import inventory_service.services.InventoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderReservationRequestedConsumer {
    private static final Logger logger =
        LoggerFactory.getLogger(OrderReservationRequestedConsumer.class);

    private final InventoryManagementService inventoryManagementService;
    private final InventoryReservationResultPublisher resultPublisher;

    public OrderReservationRequestedConsumer(
        InventoryManagementService inventoryManagementService,
        InventoryReservationResultPublisher resultPublisher
    ) {
        this.inventoryManagementService = inventoryManagementService;
        this.resultPublisher = resultPublisher;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.reserva-solicitada}",
        groupId = "${matchpass.config.kafka.consumer-group}",
        containerFactory = "orderReservationKafkaListenerContainerFactory"
    )
    public void consume(OrderReservationRequestedEvent event) {
        List<String> requestedSeatTags = event.items()
            .stream()
            .map(OrderReservationItemEvent::seatTag)
            .toList();

        logger.info(
            "Solicitação de reserva recebida. Pedido: {}. Usuário: {}. Assentos: {}.",
            event.orderId(),
            event.userId(),
            requestedSeatTags
        );

        try {
            List<String> reservedSeatTags =
                inventoryManagementService.reserveSeats(
                    event.userId(),
                    requestedSeatTags
                );

            resultPublisher.publishSuccess(
                event.orderId(),
                reservedSeatTags
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
                "Falha ao reservar os assentos do pedido {}. Motivo: {}.",
                event.orderId(),
                reason
            );

            resultPublisher.publishFailure(
                event.orderId(),
                requestedSeatTags,
                reason
            );
        }
    }
}