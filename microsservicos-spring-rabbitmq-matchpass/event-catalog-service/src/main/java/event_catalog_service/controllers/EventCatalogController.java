package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.EventCatalogContract;
import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import event_catalog_service.services.EventCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventCatalogController implements EventCatalogContract {
    private final EventCatalogService eventCatalogService;

    public EventCatalogController(EventCatalogService eventCatalogService) {
        this.eventCatalogService = eventCatalogService;
    }

    @Override
    public ResponseEntity<EventDetailsResponseDTO> findEventById(String id) {
        return ResponseEntity.ok().body(eventCatalogService.findEventById(id));
    }

    @Override
    public ResponseEntity<EventDetailsResponseDTO> createEvent(CreateEventRequestDTO dto) {
        return ResponseEntity.ok().body(eventCatalogService.registerEvent(dto));
    }

    @Override
    public ResponseEntity<SectorPricingResponseDTO> addSectorToAnEvent(String id, SectorPricingRequestDTO dto) {
        return ResponseEntity.ok().body(eventCatalogService.addSectorToAnEvent(id, dto));
    }

    @Override
    public ResponseEntity<Void> removeSectorFromAnEvent(String id, String secId) {
        eventCatalogService.removeSectorFromAnEvent(id, secId);
        return ResponseEntity.noContent().build();
    }
}