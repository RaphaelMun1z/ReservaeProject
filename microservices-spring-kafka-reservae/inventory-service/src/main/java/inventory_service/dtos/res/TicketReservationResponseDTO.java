package inventory_service.dtos.res;

import inventory_service.entities.enums.ReservationStatusEnum;

public record TicketReservationResponseDTO(
    String reservationId,
    String orderId,
    String userId,
    String eventId,
    String sectorId,
    int quantity,
    ReservationStatusEnum status,
    String environment
) {
}
