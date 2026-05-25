package event_catalog_service.dtos.res;

import java.math.BigDecimal;
import java.util.UUID;

public record SectorPricingResponseDTO(
    UUID sectorId,
    String sectorName,
    BigDecimal basePrice,
    Boolean hasNumberedSeats
) {
}
