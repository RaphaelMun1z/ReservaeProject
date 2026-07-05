package order_service.messaging.consumer;

import order_service.messaging.event.inventory.InventoryReservationResultEvent;
import order_service.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationResultConsumer {
    private final OrderService orderService;

    public InventoryReservationResultConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.resultado-reserva}",
        containerFactory = "inventoryReservationKafkaListenerContainerFactory"
    )
    public void consume(InventoryReservationResultEvent event) {
        orderService.handleInventoryReservationResult(event);
    }
}