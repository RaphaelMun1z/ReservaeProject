package order_service.messaging.event.payment;

public record PaymentRequestedItemEvent(
    String name,
    Long unitAmount,
    Long quantity
) {
}