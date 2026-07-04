package inventory_service.dtos.res;

public record TicketResponseDTO(
        String ticketTag,
        String eventId,
        String sectorId,
        String environment
) {
}
