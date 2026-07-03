package order_service.messaging.event;

import java.math.BigDecimal;

public record PaymentRequestedEvent(
        String orderId,
        String userId,
        String customerEmail,
        BigDecimal totalAmount,
        String currency
) {
}