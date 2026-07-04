package order_service.messaging.dispatcher;

import order_service.messaging.event.OrderReservationRequestedEvent;
import order_service.messaging.publisher.OrderReservationRequestedPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderReservationRequestedDispatcher {
    private final OrderReservationRequestedPublisher publisher;

    public OrderReservationRequestedDispatcher(OrderReservationRequestedPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(OrderReservationRequestedEvent event) {
        publisher.publish(event);
    }
}