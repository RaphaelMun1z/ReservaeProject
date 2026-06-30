package order_service.messaging.publisher;

import order_service.messaging.event.OrderReservationRequestedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderReservationRequestedDispatcher {
    private final OrderEventPublisher orderEventPublisher;

    public OrderReservationRequestedDispatcher(
        OrderEventPublisher orderEventPublisher
    ) {
        this.orderEventPublisher = orderEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(OrderReservationRequestedEvent event) {
        orderEventPublisher.publish(event);
    }
}