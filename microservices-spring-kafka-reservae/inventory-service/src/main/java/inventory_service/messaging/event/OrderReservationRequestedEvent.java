package inventory_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record OrderReservationRequestedEvent(
    String messageId,
    String orderId,
    String eventId,
    String userId,
    List<OrderReservationItemEvent> items,
    LocalDateTime occurredAt
) {
}