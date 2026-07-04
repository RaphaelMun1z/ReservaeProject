package payment_service.dtos.res;

public record PaymentSessionResponseDTO(
    String orderId,
    String externalPaymentId,
    String paymentUrl
) {
}