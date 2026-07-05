package event_catalog_service.dtos.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record CreateVenueRequestDTO(
    @NotNull String name,
    @NotNull String city,
    @NotNull String state,
    @NotNull @PositiveOrZero Integer totalCapacity,
    @NotNull List<SectorRequestDTO> sectors
) {
}
