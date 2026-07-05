package order_service.proxy.inventory;

import java.time.LocalDateTime;

public record TicketStatusResponseDTO(
    String ticketId,
    TicketStatusEnum status,
    LocalDateTime expiresAt,
    String environment
) {
}
