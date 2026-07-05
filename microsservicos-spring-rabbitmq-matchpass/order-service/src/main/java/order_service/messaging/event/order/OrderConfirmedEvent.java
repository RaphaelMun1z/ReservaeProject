package order_service.messaging.event.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderConfirmedEvent(
    String messageId,
    String orderId,
    String userId,
    String eventId,
    BigDecimal totalAmount,
    List<OrderConfirmedItemEvent> items,
    LocalDateTime occurredAt
) {
}