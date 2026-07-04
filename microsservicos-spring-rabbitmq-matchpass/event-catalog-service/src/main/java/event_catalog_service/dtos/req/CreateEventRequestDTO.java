package event_catalog_service.dtos.req;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequestDTO(
    String title,
    LocalDateTime eventDate,
    String venueId,
    List<SectorPricingRequestDTO> sectorsPricing
) {
}
