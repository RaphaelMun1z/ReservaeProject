package order_service.proxy.payment;

public record StripeResponseDTO(
    String status,
    String message,
    String sessionId,
    String sessionUrl
) {
}