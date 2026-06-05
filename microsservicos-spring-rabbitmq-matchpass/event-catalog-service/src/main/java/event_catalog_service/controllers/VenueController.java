package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.VenueContract;
import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.services.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VenueController implements VenueContract {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    public ResponseEntity<List<VenueResponseDTO>> findAllVenuesWithSectors() {
        return ResponseEntity.ok().body(venueService.findAllVenuesWithSectors());
    }

    @Override
    public ResponseEntity<VenueResponseDTO> findVenueById(String id) {
        return ResponseEntity.ok(venueService.findVenueById(id));
    }

    @Override
    public ResponseEntity<List<VenueResponseDTO>> findVenuesByLocation(String city, String state) {
        return ResponseEntity.ok(venueService.findVenuesByLocation(city, state));
    }

    @Override
    public ResponseEntity<VenueResponseDTO> createVenue(CreateVenueRequestDTO dto) {
        return ResponseEntity.ok(venueService.createVenue(dto));
    }

    @Override
    public ResponseEntity<SectorResponseDTO> addSectorToVenue(String id, SectorRequestDTO dto) {
        return ResponseEntity.ok(venueService.addSectorToVenue(dto, id));
    }

    @Override
    public ResponseEntity<Void> removeSectorFromVenue(String venueId, String sectorId) {
        venueService.removeSectorFromVenue(venueId, sectorId);
        return ResponseEntity.noContent().build();
    }
}