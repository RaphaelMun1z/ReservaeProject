package notification_service.messaging.event;

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