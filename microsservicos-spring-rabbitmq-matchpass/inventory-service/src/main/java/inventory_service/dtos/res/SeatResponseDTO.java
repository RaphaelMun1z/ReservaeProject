package inventory_service.dtos.res;

public record SeatResponseDTO(
    String seatTag,
    String eventId,
    String sectorId,
    String environment
) {
}
