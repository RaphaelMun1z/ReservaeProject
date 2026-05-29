package event_catalog_service.services.dataHandler;

import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import event_catalog_service.entities.*;
import event_catalog_service.repositories.*;

import java.util.List;

public class EventCatalogDataHandler {
    protected final EventRepository eventRepository;
    protected final VenueRepository venueRepository;
    protected final SectorRepository sectorRepository;
    protected final EventSectorPricingRepository eventSectorPricingRepository;
    protected final TeamRepository teamRepository;

    public EventCatalogDataHandler(EventRepository eventRepository, VenueRepository venueRepository, SectorRepository sectorRepository, EventSectorPricingRepository eventSectorPricingRepository, TeamRepository teamRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.sectorRepository = sectorRepository;
        this.eventSectorPricingRepository = eventSectorPricingRepository;
        this.teamRepository = teamRepository;
    }

    protected Event getEventById(String id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    protected Sector getSectorById(String id) {
        return sectorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado"));
    }

    protected Event createEventInstance(
        CreateEventRequestDTO dto,
        Venue venue,
        Team homeTeam,
        Team awayTeam
    ) {
        Event newEvent = new Event(
            dto.title(),
            dto.eventDate(),
            venue.getId(),
            homeTeam.getId(),
            awayTeam.getId()
        );
        return eventRepository.save(newEvent);
    }

    protected EventSectorPricing createEventSectorPricingInstance(Event event, SectorPricingRequestDTO dto) {
        EventSectorPricing newESP = new EventSectorPricing(
            event,
            dto.sectorId(),
            dto.basePrice(),
            dto.halfPrice()
        );
        return eventSectorPricingRepository.save(newESP);
    }

    protected SectorPricingResponseDTO createSectorPricingResponse(Sector sector, EventSectorPricing esp) {
        return new SectorPricingResponseDTO(
            sector.getId(),
            sector.getName(),
            esp.getBasePrice(),
            esp.getHalfPrice(),
            sector.getHasNumberedSeats()
        );
    }

    protected EventDetailsResponseDTO createEventDetailsResponseDTO(Event event, Venue venue, Team homeTeam, Team awayTeam, List<EventSectorDetailsDTO> sectorDetails) {
        return new EventDetailsResponseDTO(
            event.getId(),
            event.getTitle(),
            event.getEventDate(),
            event.getStatus(),
            venue.getName(),
            venue.getCity(),
            venue.getState(),
            homeTeam.getName(),
            awayTeam.getName(),
            sectorDetails
        );
    }
}
