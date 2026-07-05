package inventory_service.messaging.event;

public record OrderReservationItemEvent(
    String sectorId,
    int quantity
) {
}