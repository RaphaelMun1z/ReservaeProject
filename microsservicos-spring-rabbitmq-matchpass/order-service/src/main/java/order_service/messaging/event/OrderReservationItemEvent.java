package order_service.messaging.event;

public record OrderReservationItemEvent(
    String sectorId,
    String ticketId
) {
}