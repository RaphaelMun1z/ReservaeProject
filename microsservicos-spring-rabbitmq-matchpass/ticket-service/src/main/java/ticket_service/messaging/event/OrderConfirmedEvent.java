package ticket_service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderConfirmedEvent(
    String messageId,
    String orderId,
    String eventId,
    String userId,
    BigDecimal totalAmount,
    List<OrderConfirmedItemEvent> items,
    LocalDateTime occurredAt
) {
}