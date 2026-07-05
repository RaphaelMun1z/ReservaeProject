package ticket_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record TicketGeneratedEvent(
    String messageId,
    String orderId,
    String eventId,
    String userId,
    List<TicketGeneratedItemEvent> tickets,
    LocalDateTime occurredAt
) {
}