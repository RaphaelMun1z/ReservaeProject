package ticket_service.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GenerateTicketRequestDTO(
        @NotBlank String orderId,
        @NotBlank String eventId,
        @NotBlank String userId,
        @NotBlank String sectorId,
        @NotEmpty List<String> ticketTags
) {
}