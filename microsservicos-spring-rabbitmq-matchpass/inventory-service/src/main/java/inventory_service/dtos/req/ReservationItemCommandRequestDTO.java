package inventory_service.dtos.req;

public record ReservationItemCommandRequestDTO(
    String eventId,
    String sectorId,
    int quantity
) {
}