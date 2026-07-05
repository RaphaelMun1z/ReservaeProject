package order_service.messaging.event.order;

import java.math.BigDecimal;

public record OrderConfirmedItemEvent(
    String orderItemId,
    String sectorId,
    String reservationId,
    String ticketType,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {
}