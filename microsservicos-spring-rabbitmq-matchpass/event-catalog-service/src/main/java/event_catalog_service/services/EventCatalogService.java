package event_catalog_service.services;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.TeamRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.dtos.res.TeamResponseDTO;
import event_catalog_service.entities.Event;
import event_catalog_service.entities.EventSectorPricing;
import event_catalog_service.entities.Team;
import event_catalog_service.entities.Venue;
import event_catalog_service.repositories.EventRepository;
import event_catalog_service.repositories.EventSectorPricingRepository;
import event_catalog_service.repositories.TeamRepository;
import event_catalog_service.repositories.VenueRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCatalogService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventSectorPricingRepository eventSectorPricingRepository;
    private final TeamRepository teamRepository;

    public EventCatalogService(EventRepository eventRepository, VenueRepository venueRepository, TeamRepository teamRepository, EventSectorPricingRepository eventSectorPricingRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.teamRepository = teamRepository;
        this.eventSectorPricingRepository = eventSectorPricingRepository;
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
        Event newEvent = new Event(
            dto.title(),
            dto.eventDate(),
            eventVenue.getId(),
            homeTeam.getId(),
            awayTeam.getId()
        );
        Event saveEvent = eventRepository.save(newEvent);

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
        return new EventDetailsResponseDTO(
            saveEvent.getId(),
            saveEvent.getTitle(),
            saveEvent.getEventDate(),
            saveEvent.getStatus(),
            eventVenue.getName(),
            eventVenue.getCity(),
            eventVenue.getState(),
            homeTeam.getName(),
            awayTeam.getName(),
            eventSectorsDetails
        );
    }

    @Transactional
    public TeamResponseDTO createTeam(TeamRequestDTO dto) {
        Team newTeam = new Team(dto.name());
        Team savedTeam = teamRepository.save(newTeam);
        return new TeamResponseDTO(
            savedTeam.getId(),
            savedTeam.getName()
        );
    }
}