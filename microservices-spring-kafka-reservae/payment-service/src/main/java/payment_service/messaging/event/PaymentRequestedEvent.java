package payment_service.messaging.event;

import java.util.List;

public record PaymentRequestedEvent(
    String orderId,
    String userId,
    String currency,
    List<PaymentRequestedItemEvent> items
) {
}