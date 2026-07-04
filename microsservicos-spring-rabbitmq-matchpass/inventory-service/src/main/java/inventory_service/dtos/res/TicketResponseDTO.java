package inventory_service.dtos.res;

public record TicketResponseDTO(
    String ticketId,
    String eventId,
    String sectorId,
    String environment
) {
}
