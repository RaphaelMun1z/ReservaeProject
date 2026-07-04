package inventory_service.dtos.event_catalog_req;

import java.math.BigDecimal;

public record EventSectorDetailsDTO(
        String eventId,
        String sectorId,
        String sectorName,
        BigDecimal sectorBasePrice,
        BigDecimal sectorHalfPrice,
        Boolean hasNumberedTickets
) {
}
