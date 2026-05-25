package event_catalog_service.dtos.req;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventRequestDTO(
    String title,
    LocalDateTime eventDate,
    UUID venueId,
    UUID homeTeamId,
    UUID awayTeamId,
    List<SectorPricingRequest> sectorsPricing
) {
}
