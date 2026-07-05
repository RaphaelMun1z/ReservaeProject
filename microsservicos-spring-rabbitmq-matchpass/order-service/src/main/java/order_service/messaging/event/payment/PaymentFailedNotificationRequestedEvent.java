package order_service.messaging.event.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentFailedNotificationRequestedEvent(
    String messageId,
    String orderId,
    String userId,
    String customerName,
    String customerEmail,
    String eventId,
    String eventName,
    String eventDate,
    BigDecimal totalAmount,
    String paymentUrl,
    String orderUrl,
    String reason,
    List<PaymentNotificationItemEvent> items,
    LocalDateTime occurredAt
) {
}