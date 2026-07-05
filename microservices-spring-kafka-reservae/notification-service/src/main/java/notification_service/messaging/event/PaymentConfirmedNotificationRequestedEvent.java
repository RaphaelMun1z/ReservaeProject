package notification_service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentConfirmedNotificationRequestedEvent(
    String messageId,
    String orderId,
    String userId,
    String customerName,
    String customerEmail,
    String eventId,
    String eventName,
    String eventDate,
    BigDecimal totalAmount,
    String orderUrl,
    List<PaymentNotificationItemEvent> items,
    LocalDateTime occurredAt
) {
}