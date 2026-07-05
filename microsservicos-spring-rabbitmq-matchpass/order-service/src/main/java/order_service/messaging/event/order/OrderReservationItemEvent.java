package order_service.messaging.event.order;

public record OrderReservationItemEvent(
    String sectorId,
    int quantity
) {
}