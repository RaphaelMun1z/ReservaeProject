package order_service.dtos.res;

import order_service.entities.enums.TicketType;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
    String orderItemId,
    String sectorId,
    String reservationId,
    TicketType ticketType,
    int quantity,
    BigDecimal appliedPrice,
    BigDecimal subtotal
) {
}
