package event_catalog_service.dtos.res;

import java.util.List;

public record VenueResponseDTO(
    String id,
    String name,
    String city,
    String state,
    Integer totalCapacity,
    List<SectorResponseDTO> sectors
) {
}
