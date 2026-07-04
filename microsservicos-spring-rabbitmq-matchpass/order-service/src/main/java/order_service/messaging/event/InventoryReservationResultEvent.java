package order_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record InventoryReservationResultEvent(
        String messageId,
        String orderId,
        boolean reserved,
        List<String> ticketTags,
        String reason,
        LocalDateTime occurredAt
) {
}