package event_catalog_service.controllers.contracts;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Venue Endpoint", description = "Gerenciamento de locais de eventos (venues) e seus respectivos setores físicos")
@RequestMapping("/event-catalog-service/api/venue")
public interface VenueContract {

    @Operation(summary = "Listar todos os locais cadastrados juntamente com seus setores")
    @GetMapping("/v1")
    ResponseEntity<List<VenueResponseDTO>> findAllVenuesWithSectors();

    @Operation(summary = "Buscar os detalhes de um local específico através do seu ID")
    @GetMapping("/v1/{id}")
    ResponseEntity<VenueResponseDTO> findVenueById(@PathVariable String id);

    @Operation(summary = "Filtrar locais disponíveis por cidade e estado")
    @GetMapping("/v1/filter-by-location")
    ResponseEntity<List<VenueResponseDTO>> findVenuesByLocation(
        @RequestParam String city,
        @RequestParam String state
    );

    @Operation(summary = "Cadastrar um novo local de evento")
    @PostMapping("/v1")
    ResponseEntity<VenueResponseDTO> createVenue(@RequestBody CreateVenueRequestDTO dto);

    @Operation(summary = "Adicionar um novo setor físico a um local existente")
    @PostMapping("/v1/{id}/add-sector")
    ResponseEntity<SectorResponseDTO> addSectorToVenue(
        @PathVariable String id,
        @RequestBody SectorRequestDTO dto
    );

    @Operation(summary = "Remover um setor específico de um local")
    @DeleteMapping("/v1/{venueId}/remove-sector/{sectorId}")
    ResponseEntity<Void> removeSectorFromVenue(
        @PathVariable String venueId,
        @PathVariable String sectorId
    );
}