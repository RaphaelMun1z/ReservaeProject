package event_catalog_service.dtos.res;

import java.math.BigDecimal;

public record EventSectorDetailsDTO(
        String eventId,
        String sectorId,
        String sectorName,
        BigDecimal sectorBasePrice,
        BigDecimal sectorHalfPrice,
        Boolean hasNumberedTickets,
        Integer totalCapacity
) {
}
