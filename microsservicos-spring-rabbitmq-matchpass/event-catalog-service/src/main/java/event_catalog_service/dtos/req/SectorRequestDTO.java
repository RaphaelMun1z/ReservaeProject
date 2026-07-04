package event_catalog_service.dtos.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SectorRequestDTO(
        @NotNull String name,
        @NotNull @PositiveOrZero Integer capacity,
        @NotNull Boolean hasNumberedTickets
) {
}
