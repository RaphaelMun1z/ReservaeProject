package order_service.messaging.event.payment;

import java.time.Instant;

public record PaymentApprovedEvent(String orderId, String externalPaymentId, String paymentIntentId, Long paidAmount,
                                   String currency, Instant occurredAt) {
}