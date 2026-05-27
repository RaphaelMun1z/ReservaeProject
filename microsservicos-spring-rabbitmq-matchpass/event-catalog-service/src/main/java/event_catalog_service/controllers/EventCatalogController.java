package event_catalog_service.controllers;

import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.req.TeamRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.TeamResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.services.EventCatalogService;
import event_catalog_service.services.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-catalog")
public class EventCatalogController {
    private final EventCatalogService eventCatalogService;
    private final VenueService venueService;

    public EventCatalogController(EventCatalogService eventCatalogService, VenueService venueService) {
        this.eventCatalogService = eventCatalogService;
        this.venueService = venueService;
    }

    @GetMapping("/v1/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/v1/event/{id}")
    public ResponseEntity<EventDetailsResponseDTO> findEventById(@PathVariable String id) {
        return ResponseEntity.ok().body(eventCatalogService.findEventById(id));
    }

    @PostMapping("/v1/event")
    public ResponseEntity<EventDetailsResponseDTO> createEvent(@RequestBody CreateEventRequestDTO dto) {
        return ResponseEntity.ok().body(eventCatalogService.createEvent(dto));
    }

    @PostMapping("/v1/team")
    public ResponseEntity<TeamResponseDTO> createTeam(@RequestBody TeamRequestDTO dto) {
        return ResponseEntity.ok().body(eventCatalogService.createTeam(dto));
    }

    @PostMapping("/v1/venue")
    public ResponseEntity<VenueResponseDTO> createVenue(@RequestBody CreateVenueRequestDTO dto) {
        return ResponseEntity.ok().body(venueService.createVenue(dto));
    }

    @GetMapping("/v1/venue")
    public ResponseEntity<List<VenueResponseDTO>> getAllVenues() {
        return ResponseEntity.ok().body(venueService.getAllVenues());
    }

    @GetMapping("/v1/venue/{id}")
    public ResponseEntity<VenueResponseDTO> getVenue(@PathVariable String id) {
        return ResponseEntity.ok().body(venueService.getVenueById(id));
    }

    @PostMapping("v1/venue/{id}/add-sector")
    public ResponseEntity<SectorResponseDTO> addSectorToVenue(@PathVariable String id, @RequestBody SectorRequestDTO dto) {
        return ResponseEntity.ok().body(venueService.addSectorToVenue(dto, id));
    }
}