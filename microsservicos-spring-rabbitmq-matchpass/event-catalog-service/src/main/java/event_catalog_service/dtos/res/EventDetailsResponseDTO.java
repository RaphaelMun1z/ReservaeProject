package event_catalog_service.dtos.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventDetailsResponseDTO(
    UUID eventId,
    String title,
    LocalDateTime eventDate,
    VenueDto venue,
    List<SectorPricingResponse> availableSectors
) {
}
