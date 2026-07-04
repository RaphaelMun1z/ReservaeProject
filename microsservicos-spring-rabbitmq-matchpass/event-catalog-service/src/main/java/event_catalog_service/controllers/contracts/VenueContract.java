package event_catalog_service.controllers.contracts;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Venue Endpoint", description = "Gerenciamento de locais de eventos (venues) e seus respectivos setores físicos")
public interface VenueContract {

    @Operation(summary = "Listar todos os locais cadastrados juntamente com seus setores")
    ResponseEntity<List<VenueResponseDTO>> findAllVenuesWithSectors();

    @Operation(summary = "Buscar os detalhes de um local específico através do seu ID")
    ResponseEntity<VenueResponseDTO> findVenueById(String id);

    @Operation(summary = "Filtrar locais disponíveis por cidade e estado")
    ResponseEntity<List<VenueResponseDTO>> findVenuesByLocation(
        String city,
        String state
    );

    @Operation(summary = "Cadastrar um novo local de evento")
    ResponseEntity<VenueResponseDTO> createVenue(CreateVenueRequestDTO dto);

    @Operation(summary = "Adicionar um novo setor físico a um local existente")
    ResponseEntity<SectorResponseDTO> addSectorToVenue(
        String id,
        SectorRequestDTO dto
    );

    @Operation(summary = "Remover um setor específico de um local")
    ResponseEntity<Void> removeSectorFromVenue(
        String venueId,
        String sectorId
    );
}