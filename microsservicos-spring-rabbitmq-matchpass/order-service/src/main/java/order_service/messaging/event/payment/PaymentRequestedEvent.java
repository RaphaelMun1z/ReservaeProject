package order_service.messaging.event.payment;

import java.util.List;

public record PaymentRequestedEvent(
    String orderId,
    String userId,
    String currency,
    List<PaymentRequestedItemEvent> items
) {
}