package inventory_service.dtos.res;

import inventory_service.entities.enums.TicketStatusEnum;

import java.time.LocalDateTime;

public record TicketStatusResponseDTO(
        String ticketTag,
        TicketStatusEnum status,
        LocalDateTime expiresAt,
        String environment
) {
}
