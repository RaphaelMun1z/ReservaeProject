package order_service.proxy.inventory;

import java.time.LocalDateTime;

public record TicketStatusResponseDTO(
        String ticketTag,
        TicketStatusEnum status,
        LocalDateTime expiresAt,
        String environment
) {
}
