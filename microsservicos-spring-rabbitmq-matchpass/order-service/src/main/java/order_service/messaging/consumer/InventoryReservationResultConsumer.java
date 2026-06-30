package order_service.messaging.consumer;

import order_service.messaging.event.InventoryReservationResultEvent;
import order_service.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationResultConsumer {
    private static final Logger logger =
        LoggerFactory.getLogger(InventoryReservationResultConsumer.class);

    private final OrderService orderService;

    public InventoryReservationResultConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.resultado-reserva}",
        groupId = "${matchpass.config.kafka.consumer-group}",
        containerFactory = "inventoryReservationKafkaListenerContainerFactory"
    )
    public void consume(InventoryReservationResultEvent event) {
        logger.info(
            "Resultado da reserva recebido. Pedido: {}. Reservado: {}.",
            event.orderId(),
            event.reserved()
        );

        orderService.handleInventoryReservationResult(event);
    }
}