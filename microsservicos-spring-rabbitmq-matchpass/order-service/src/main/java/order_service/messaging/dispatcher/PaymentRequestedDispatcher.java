package order_service.messaging.dispatcher;

import order_service.messaging.event.PaymentRequestedEvent;
import order_service.messaging.publisher.PaymentRequestedPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentRequestedDispatcher {
    private final PaymentRequestedPublisher publisher;

    public PaymentRequestedDispatcher(PaymentRequestedPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(PaymentRequestedEvent event) {
        publisher.publish(event);
    }
}