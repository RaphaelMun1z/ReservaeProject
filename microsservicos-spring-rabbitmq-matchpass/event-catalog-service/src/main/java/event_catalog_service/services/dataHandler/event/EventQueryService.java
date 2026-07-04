package event_catalog_service.services.dataHandler.event;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.entities.Event;
import event_catalog_service.entities.Sector;
import event_catalog_service.entities.Venue;
import event_catalog_service.repositories.EventRepository;
import event_catalog_service.repositories.SectorRepository;
import event_catalog_service.repositories.VenueRepository;
import org.springframework.stereotype.Component;

@Component
public class EventQueryService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SectorRepository sectorRepository;

    public EventQueryService(
        EventRepository eventRepository,
        VenueRepository venueRepository,
        SectorRepository sectorRepository
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.sectorRepository = sectorRepository;
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    public EventDetailsProjection getEventDetailsById(String id) {
        return eventRepository.findEventDetailsByEventId(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    public Venue getVenueById(String id) {
        return venueRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Local de evento não encontrado com o ID: " + id));
    }

    public Sector getSectorById(String id) {
        return sectorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado"));
    }
}
