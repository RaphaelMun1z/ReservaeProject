package event_catalog_service.services;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.req.SectorsTicketPriceRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.dtos.res.EventSectorPriceResponseDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import event_catalog_service.entities.Event;
import event_catalog_service.entities.EventSectorPricing;
import event_catalog_service.entities.Venue;
import event_catalog_service.exceptions.models.NotFoundException;
import event_catalog_service.repositories.EventRepository;
import event_catalog_service.repositories.EventSectorPricingRepository;
import event_catalog_service.services.dataHandler.event.EventMapper;
import event_catalog_service.services.dataHandler.event.EventQueryService;
import event_catalog_service.services.dataHandler.event.EventValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCatalogService {
    private final EventRepository eventRepository;
    private final EventSectorPricingRepository eventSectorPricingRepository;

    private final EventQueryService eventQuery;
    private final EventMapper mapper;
    private final EventValidator validator;

    public EventCatalogService(
        EventRepository eventRepository,
        EventSectorPricingRepository eventSectorPricingRepository,
        EventQueryService eventQuery,
        EventMapper mapper,
        EventValidator validator
    ) {
        this.eventRepository = eventRepository;
        this.eventSectorPricingRepository = eventSectorPricingRepository;
        this.eventQuery = eventQuery;
        this.mapper = mapper;
        this.validator = validator;
    }

    public EventDetailsResponseDTO findEventById(String eventId) {
        EventDetailsProjection event = eventQuery.getEventDetailsById(eventId);
        List<EventSectorDetailsDTO> eventSectorsDetailsDTO = eventSectorPricingRepository.findEventSectorsDetailsByEventId(
            eventId);
        return mapper.toEventDetailsResponseDTO(
            event,
            eventSectorsDetailsDTO
        );
    }

    @Transactional
    public EventDetailsResponseDTO registerEvent(CreateEventRequestDTO dto) {
        Venue venue = eventQuery.getVenueById(dto.venueId());

        Event newEvent = new Event(
            dto.title(),
            dto.eventDate(),
            venue.getId()
        );
        Event savedEvent = eventRepository.save(newEvent);

        List<EventSectorPricing> eventSectorPricingList =
            dto.sectorsPricing()
                .stream()
                .map(sectorPricing -> new EventSectorPricing(
                    savedEvent,
                    sectorPricing.sectorId(),
                    sectorPricing.basePrice(),
                    sectorPricing.halfPrice()
                ))
                .toList();
        eventSectorPricingRepository.saveAll(eventSectorPricingList);

        List<EventSectorDetailsDTO> eventSectorsDetails = eventSectorPricingRepository.findEventSectorsDetailsByEventId(
            savedEvent.getId());

        return mapper.toEventDetailsResponseDTO(
            savedEvent,
            venue,
            eventSectorsDetails
        );
    }

    @Transactional
    public SectorPricingResponseDTO addSectorToAnEvent(String eventId, SectorPricingRequestDTO dto) {
        Event eventFound = eventQuery.getEventById(eventId);

        validator.validateDuplicatedSector(
            eventId,
            dto.sectorId()
        );

        EventSectorPricing newESP = new EventSectorPricing(
            eventFound,
            dto.sectorId(),
            dto.basePrice(),
            dto.halfPrice()
        );
        EventSectorPricing savedESP = eventSectorPricingRepository.save(newESP);

        eventFound.addPricing(savedESP);
        return mapper.toSectorPricingResponseDTO(
            eventQuery.getSectorById(dto.sectorId()),
            savedESP
        );
    }

    @Transactional
    public void removeSectorFromAnEvent(String eventId, String secId) {
        Event eventFound = eventQuery.getEventById(eventId);
        EventSectorPricing eventSectorPricingFound =
            eventFound.getPricings()
                .stream()
                .filter(eventSectorPricing -> eventSectorPricing.getSectorId()
                    .equals(secId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Setor não encontrado no evento"));
        eventFound.removePricing(eventSectorPricingFound);
    }

    public List<EventSectorPriceResponseDTO> consultTicketsPrice(SectorsTicketPriceRequestDTO dto) {
        return dto.sectorsId().stream().map(
            sectorId -> {
                EventSectorDetailsDTO details = eventSectorPricingRepository.findEventSectorDetailsByEventIdAndSectorId(dto.eventId(), sectorId).orElseThrow(() -> new NotFoundException("Evento ou Setor não encontrado!"));
                return new EventSectorPriceResponseDTO(
                    details.eventId(),
                    details.sectorId(),
                    details.sectorBasePrice(),
                    details.sectorHalfPrice()
                );
            }
        ).toList();
    }
}