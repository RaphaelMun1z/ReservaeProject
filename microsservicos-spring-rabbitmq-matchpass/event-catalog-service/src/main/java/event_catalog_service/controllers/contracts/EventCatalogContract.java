package event_catalog_service.controllers.contracts;

import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Event Catalog Endpoint", description = "Gerenciamento do catálogo de eventos e configuração de setores/preços")
@RequestMapping("/event-catalog-service/api/event")
public interface EventCatalogContract {

    @Operation(summary = "Buscar os detalhes completos de um evento através do seu ID")
    @GetMapping("/v1/{id}")
    ResponseEntity<EventDetailsResponseDTO> findEventById(@PathVariable String id);

    @Operation(summary = "Registrar um novo evento no catálogo")
    @PostMapping("/v1")
    ResponseEntity<EventDetailsResponseDTO> createEvent(@RequestBody CreateEventRequestDTO dto);

    @Operation(summary = "Adicionar um novo setor (e sua precificação) a um evento existente")
    @PostMapping("/v1/{id}/add-sector")
    ResponseEntity<SectorPricingResponseDTO> addSectorToAnEvent(
        @PathVariable String id,
        @RequestBody SectorPricingRequestDTO dto
    );

    @Operation(summary = "Remover um setor específico de um evento")
    @DeleteMapping("/v1/{id}/remove-sector/{secId}")
    ResponseEntity<Void> removeSectorFromAnEvent(
        @PathVariable String id,
        @PathVariable String secId
    );
}