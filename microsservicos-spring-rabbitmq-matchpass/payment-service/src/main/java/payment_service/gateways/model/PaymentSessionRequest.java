package payment_service.gateways.model;

public record PaymentSessionRequest(
    String orderId,
    String userId,
    Long amount,
    Long quantity,
    String productName,
    String currency,
    String customerEmail
) {
}