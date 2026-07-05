package payment_service.gateways.model;

import java.util.List;

public record PaymentSessionRequest(
    String orderId,
    String userId,
    String currency,
    String customerEmail,
    List<PaymentSessionItemRequest> items
) {
}