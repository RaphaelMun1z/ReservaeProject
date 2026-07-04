package event_catalog_service.dtos.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSummaryResponseDTO(
    String eventId,
    String title,
    LocalDateTime eventDate,
    String venueName,
    String venueCity,
    BigDecimal startingPrice
) {
}