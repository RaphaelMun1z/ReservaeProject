package inventory_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record InventoryReservationResultEvent(
    String messageId,
    String orderId,
    boolean reserved,
    List<String> ticketsId,
    String reason,
    LocalDateTime occurredAt
) {
}