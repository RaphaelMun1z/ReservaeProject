package payment_service.messaging.event;

import java.time.Instant;

public record PaymentFailedEvent(
        String orderId,
        String externalPaymentId,
        String reason,
        Instant occurredAt
) {
}
