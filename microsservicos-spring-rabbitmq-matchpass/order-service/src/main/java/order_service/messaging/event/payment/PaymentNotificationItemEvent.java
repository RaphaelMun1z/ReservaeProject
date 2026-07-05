package order_service.messaging.event.payment;

import java.math.BigDecimal;

public record PaymentNotificationItemEvent(
    String sectorId,
    String sectorName,
    String ticketType,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal,
    String imageUrl
) {
}