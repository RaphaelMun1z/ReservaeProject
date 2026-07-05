package inventory_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record InventoryReservationResultEvent(
    String eventId,
    String orderId,
    boolean success,
    List<String> reservationIds,
    String failureReason,
    LocalDateTime occurredAt
) {
}