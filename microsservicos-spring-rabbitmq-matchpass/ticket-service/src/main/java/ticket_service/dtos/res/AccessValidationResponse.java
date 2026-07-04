package ticket_service.dtos.res;

import ticket_service.entities.enums.AccessResultEnum;

public record AccessValidationResponse(
    Boolean isAllowed,
    AccessResultEnum result,
    String message,
    String sectorName,
    String ticketId
) {
}
