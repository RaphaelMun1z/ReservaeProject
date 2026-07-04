package inventory_service.messaging.consumer;

import inventory_service.messaging.event.OrderReservationRequestedEvent;
import inventory_service.services.ReservationRequestHandlerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderReservationRequestedConsumer {
    private final ReservationRequestHandlerService reservationRequestHandlerService;

    public OrderReservationRequestedConsumer(
        ReservationRequestHandlerService reservationRequestHandlerService
    ) {
        this.reservationRequestHandlerService = reservationRequestHandlerService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.reserva-solicitada}",
        groupId = "${matchpass.config.kafka.consumer-group}",
        containerFactory = "orderReservationKafkaListenerContainerFactory"
    )
    public void consume(OrderReservationRequestedEvent event) {
        reservationRequestHandlerService.handle(event);
    }
}