package event_catalog_service.services.dataHandler.venue;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class VenueValidator {
    public void validateTotalCapacity(CreateVenueRequestDTO dto) {
        int totalCapacityExpected = dto.totalCapacity();

        int totalCapacityCount =
            dto.sectors()
                .stream()
                .mapToInt(SectorRequestDTO::capacity)
                .sum();

        if (totalCapacityCount != totalCapacityExpected) {
            throw new IllegalArgumentException("A capacidade total informada e a soma das capacidades dos setores não são iguais.");
        }
    }
}
