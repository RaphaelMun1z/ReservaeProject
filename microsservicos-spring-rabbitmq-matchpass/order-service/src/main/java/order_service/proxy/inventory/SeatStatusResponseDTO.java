package order_service.proxy.inventory;

import java.time.LocalDateTime;

public record SeatStatusResponseDTO(
    String seatTag,
    SeatStatusEnum status,
    LocalDateTime expiresAt,
    String environment
) {
}
