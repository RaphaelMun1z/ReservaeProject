package inventory_service.dtos.req;

public record TicketReservationRequestDTO(
    String eventId,
    String sectorId,
    String ticketId
) {
}