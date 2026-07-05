package ticket_service.dtos.res;

import ticket_service.entities.enums.AccessStatusEnum;

public record AccessValidationResponseDTO(
    Boolean isAllowed,
    AccessStatusEnum result,
    String message,
    String sectorName,
    String ticketId
) {
}
