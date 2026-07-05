package payment_service.gateways.model;

public record PaymentSessionItemRequest(
    String name,
    Long unitAmount,
    Long quantity
) {
}