package inventory_service.messaging.event;

public record OrderReservationItemEvent(
    String eventId,
    String sectorId,
    int quantity
) {
}