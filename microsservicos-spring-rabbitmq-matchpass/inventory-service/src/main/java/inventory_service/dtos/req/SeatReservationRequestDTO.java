package inventory_service.dtos.req;

public record SeatReservationRequestDTO(
    String eventId,
    String sectorId,
    Integer quantity,
    String seatNumber
) {
}
