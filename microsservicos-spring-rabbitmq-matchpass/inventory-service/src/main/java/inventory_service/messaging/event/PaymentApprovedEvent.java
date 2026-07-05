package inventory_service.messaging.event;

import java.time.Instant;

public record PaymentApprovedEvent(
    String orderId,
    String checkoutSessionId,
    String paymentIntentId,
    Long amount,
    String currency,
    Instant occurredAt
) {
}