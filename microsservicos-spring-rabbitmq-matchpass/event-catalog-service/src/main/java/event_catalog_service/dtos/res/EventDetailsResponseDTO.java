package event_catalog_service.dtos.res;

import event_catalog_service.entities.enums.EventStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

public record EventDetailsResponseDTO(
    String eventId,
    String title,
    LocalDateTime eventDate,
    EventStatusEnum status,
    String venueName,
    String venueCity,
    String venueState,
    List<EventSectorDetailsDTO> sectorsDetails
) {
}
