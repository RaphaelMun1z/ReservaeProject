package event_catalog_service.services.dataHandler.event;

import event_catalog_service.repositories.EventSectorPricingRepository;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {
    private final EventSectorPricingRepository eventSectorPricingRepository;

    public EventValidator(EventSectorPricingRepository eventSectorPricingRepository) {
        this.eventSectorPricingRepository = eventSectorPricingRepository;
    }

    public void validateDuplicatedSector(String eventId, String sectorId) {
        boolean sectorAlreadyExists =
            eventSectorPricingRepository
                .existsByEvent_IdAndSectorId(
                    eventId,
                    sectorId
                );

        if (sectorAlreadyExists) {
            throw new IllegalArgumentException("O Setor já está associado a esse evento.");
        }
    }
}
