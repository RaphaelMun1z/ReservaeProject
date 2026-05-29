package event_catalog_service.services;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import event_catalog_service.entities.Event;
import event_catalog_service.entities.EventSectorPricing;
import event_catalog_service.entities.Team;
import event_catalog_service.entities.Venue;
import event_catalog_service.repositories.*;
import event_catalog_service.services.dataHandler.EventCatalogDataHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCatalogService extends EventCatalogDataHandler {
    public EventCatalogService(EventRepository eventRepository, VenueRepository venueRepository, SectorRepository sectorRepository, EventSectorPricingRepository eventSectorPricingRepository, TeamRepository teamRepository) {
        super(eventRepository, venueRepository, sectorRepository, eventSectorPricingRepository, teamRepository);
    }

    public EventDetailsResponseDTO findEventById(String eventId) {
        EventDetailsProjection event = eventRepository.findEventDetailsByEventId(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        List<EventSectorDetailsDTO> eventSectorsDetailsDTO = eventSectorPricingRepository.findEventSectorsDetailsByEventId(eventId);

        return new EventDetailsResponseDTO(
            event.getEventId(),
            event.getTitle(),
            event.getEventDate(),
            event.getStatus(),

            event.getVenueName(),
            event.getVenueCity(),
            event.getVenueState(),

            event.getHomeTeamName(),
            event.getAwayTeamName(),

            eventSectorsDetailsDTO
        );
    }

    @Transactional
    public EventDetailsResponseDTO createEvent(CreateEventRequestDTO dto) {
        // Busca Local do evento, Time Mandante e Visitante
        Venue eventVenue = venueRepository.findById(dto.venueId()).orElseThrow(() -> new IllegalArgumentException("Local de evento não encontrado com o ID: " + dto.venueId()));
        Team homeTeam = teamRepository.findById(dto.homeTeamId()).orElseThrow(() -> new IllegalArgumentException("Time mandante não encontrado com o ID: " + dto.homeTeamId()));
        Team awayTeam = teamRepository.findById(dto.awayTeamId()).orElseThrow(() -> new IllegalArgumentException("Time visitante não encontrado com o ID: " + dto.awayTeamId()));

        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("O time mandante não pode ser o mesmo time visitante.");
        }

        // Cria e Salva Evento
        Event newEvent = createEventInstance(dto, eventVenue, homeTeam, awayTeam);

        // Salva Preço dos setores desse evento
        List<EventSectorPricing> eventSectorPricingList =
            dto.sectorsPricing()
                .stream()
                .map(sp -> new EventSectorPricing(
                    newEvent,
                    sp.sectorId(),
                    sp.basePrice(),
                    sp.halfPrice()
                )).toList();
        eventSectorPricingRepository.saveAll(eventSectorPricingList);

        // Busca detalhes dos setores desse evento
        List<EventSectorDetailsDTO> eventSectorsDetails = eventSectorPricingRepository.findEventSectorsDetailsByEventId(saveEvent.getId());

        // Prepara DTO para retorno
        return createEventDetailsResponseDTO(saveEvent, eventVenue, homeTeam, awayTeam, eventSectorsDetails);
    }

    @Transactional
    public SectorPricingResponseDTO addSectorToAnEvent(String eventId, SectorPricingRequestDTO dto) {
        Event eventFound = getEventById(eventId);
        EventSectorPricing savedESP = createEventSectorPricingInstance(eventFound, dto);
        eventFound.addPricing(savedESP);
        return createSectorPricingResponse(getSectorById(dto.sectorId()), savedESP);
    }

    @Transactional
    public void removeSectorFromAnEvent(String eventId, String secId) {
        Event eventFound = getEventById(eventId);
        EventSectorPricing espFound = eventFound.getPricings()
            .stream()
            .filter(eventSectorPricing -> eventSectorPricing.getSectorId().equals(secId)).toList().getFirst();
        eventFound.removePricing(espFound);
    }

}