package order_service.dtos.req;

import order_service.entities.enums.TicketType;

public record OrderItemRequestDTO(
    String sectorId,
    String seatTag,
    TicketType ticketType
) {
}
