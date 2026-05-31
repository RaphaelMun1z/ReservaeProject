package inventory_service.dtos.res;

import inventory_service.entities.enums.SeatStatusEnum;

import java.time.LocalDateTime;

public record SeatStatusResponseDTO(
    String lockId,
    SeatStatusEnum status,
    LocalDateTime expiresAt
) {
}
