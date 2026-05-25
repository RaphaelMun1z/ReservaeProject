package event_catalog_service.dtos.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventSummaryResponseDTO(
    UUID eventId,
    String title,
    LocalDateTime eventDate,
    String venueName,
    String venueCity,
    BigDecimal startingPrice
) {
}