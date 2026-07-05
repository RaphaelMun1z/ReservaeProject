package payment_service.messaging.event;

public record PaymentRequestedItemEvent(
    String name,
    Long unitAmount,
    Long quantity
) {
}