package notification_service.messaging.event;

public record PaymentSessionCreatedEvent(
    String orderId,
    String paymentId,
    String paymentUrl
) {
}