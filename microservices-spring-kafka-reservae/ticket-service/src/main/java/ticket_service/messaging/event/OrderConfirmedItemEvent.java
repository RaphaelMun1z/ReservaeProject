package ticket_service.messaging.event;

import java.math.BigDecimal;

public record OrderConfirmedItemEvent(
    String orderItemId,
    String sectorId,
    String reservationId,
    String ticketType,
    int quantity,
    BigDecimal appliedPrice
) {
}