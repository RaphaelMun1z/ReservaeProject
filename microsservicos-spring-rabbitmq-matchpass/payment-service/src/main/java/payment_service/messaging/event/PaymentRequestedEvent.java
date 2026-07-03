package payment_service.messaging.event;

public record PaymentRequestedEvent(
        String orderId,
        String userId,
        Long amount,
        Long quantity,
        String productName,
        String currency
) {
}