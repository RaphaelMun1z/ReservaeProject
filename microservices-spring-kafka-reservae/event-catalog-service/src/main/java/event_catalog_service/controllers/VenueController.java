package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.VenueContract;
import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.services.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event-catalog-service/api/venues")
public class VenueController implements VenueContract {
    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    @GetMapping("/v1")
    public ResponseEntity<List<VenueResponseDTO>> findAllVenuesWithSectors() {
        return ResponseEntity.ok().body(venueService.findAllVenuesWithSectors());
    }

    @Override
    @GetMapping("/v1/{id}")
    public ResponseEntity<VenueResponseDTO> findVenueById(@PathVariable String id) {
        return ResponseEntity.ok(venueService.findVenueById(id));
    }

    @Override
    @GetMapping("/v1/filter-by-location")
    public ResponseEntity<List<VenueResponseDTO>> findVenuesByLocation(
        @RequestParam String city,
        @RequestParam String state
    ) {
        return ResponseEntity.ok(venueService.findVenuesByLocation(
            city,
            state
        ));
    }

    @Override
    @PostMapping("/v1")
    public ResponseEntity<VenueResponseDTO> createVenue(@Valid @RequestBody CreateVenueRequestDTO dto) {
        return ResponseEntity.ok(venueService.createVenue(dto));
    }

    @Override
    @PostMapping("/v1/{id}/add-sector")
    public ResponseEntity<SectorResponseDTO> addSectorToVenue(
        @PathVariable String id,
        @Valid @RequestBody SectorRequestDTO dto
    ) {
        return ResponseEntity.ok(venueService.addSectorToVenue(dto, id));
    }

    @Override
    @DeleteMapping("/v1/{venueId}/remove-sector/{sectorId}")
    public ResponseEntity<Void> removeSectorFromVenue(
        @PathVariable String venueId,
        @PathVariable String sectorId
    ) {
        venueService.removeSectorFromVenue(
            venueId,
            sectorId
        );
        return ResponseEntity.noContent().build();
    }
}