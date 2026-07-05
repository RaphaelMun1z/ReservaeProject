package order_service.messaging.event.payment;

public record PaymentSessionCreatedEvent(
    String orderId,
    String paymentId,
    String paymentUrl
) {
}