package inventory_service.dtos.res;

import inventory_service.entities.enums.SeatStatusEnum;

import java.time.LocalDateTime;

public record SeatStatusResponseDTO(
    String seatTag,
    SeatStatusEnum status,
    LocalDateTime expiresAt,
    String environment
) {
}
