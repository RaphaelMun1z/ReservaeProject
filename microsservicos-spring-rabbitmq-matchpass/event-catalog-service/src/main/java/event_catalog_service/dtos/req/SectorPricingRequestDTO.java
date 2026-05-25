package event_catalog_service.dtos.req;

import java.math.BigDecimal;
import java.util.UUID;

public record SectorPricingRequestDTO(
    UUID sectorId,
    BigDecimal basePrice,
    BigDecimal halfPrice
) {
}