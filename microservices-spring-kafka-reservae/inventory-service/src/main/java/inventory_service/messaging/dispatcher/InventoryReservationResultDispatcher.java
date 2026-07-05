package inventory_service.messaging.dispatcher;

import inventory_service.messaging.event.InventoryReservationResultEvent;
import inventory_service.messaging.publisher.InventoryReservationResultPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InventoryReservationResultDispatcher {
    private final InventoryReservationResultPublisher publisher;

    public InventoryReservationResultDispatcher(
        InventoryReservationResultPublisher publisher
    ) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(InventoryReservationResultEvent event) {
        publisher.publish(event);
    }
}