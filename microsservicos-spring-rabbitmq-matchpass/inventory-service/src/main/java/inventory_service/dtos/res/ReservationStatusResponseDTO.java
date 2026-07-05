package inventory_service.dtos.res;

import inventory_service.entities.enums.ReservationStatusEnum;

import java.time.LocalDateTime;

public record ReservationStatusResponseDTO(
    String reservationId,
    ReservationStatusEnum status,
    LocalDateTime expiresAt,
    String environment
) {
}
