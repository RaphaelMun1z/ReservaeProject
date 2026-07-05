package inventory_service.messaging.event;

import java.time.Instant;

public record PaymentFailedEvent(
    String orderId,
    String checkoutSessionId,
    String reason,
    Instant occurredAt
) {
}