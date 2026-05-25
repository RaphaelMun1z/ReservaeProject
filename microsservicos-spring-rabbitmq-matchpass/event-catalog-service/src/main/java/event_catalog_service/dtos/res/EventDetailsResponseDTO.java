package event_catalog_service.dtos.res;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventDetailsResponseDTO(
    UUID eventId,
    String title,
    LocalDateTime eventDate,
    String venueName,
    String venueCity,
    List<SectorPricingResponseDTO> availableSectors
) {
}
