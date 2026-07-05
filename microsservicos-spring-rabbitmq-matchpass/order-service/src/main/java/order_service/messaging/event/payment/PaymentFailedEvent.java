package order_service.messaging.event.payment;

import java.time.Instant;

public record PaymentFailedEvent(String orderId, String externalPaymentId, String reason, Instant occurredAt) {
}