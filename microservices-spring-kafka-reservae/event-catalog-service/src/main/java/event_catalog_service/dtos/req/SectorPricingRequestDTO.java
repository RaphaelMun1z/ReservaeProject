package event_catalog_service.dtos.req;

import java.math.BigDecimal;

public record SectorPricingRequestDTO(
    String sectorId,
    BigDecimal basePrice,
    BigDecimal halfPrice
) {
}