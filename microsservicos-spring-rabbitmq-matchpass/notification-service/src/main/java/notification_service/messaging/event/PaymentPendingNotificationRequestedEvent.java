package notification_service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentPendingNotificationRequestedEvent(
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
    LocalDateTime paymentExpiresAt,
    List<PaymentNotificationItemEvent> items,
    LocalDateTime occurredAt
) {
}