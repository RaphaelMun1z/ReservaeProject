package event_catalog_service.controllers.contracts;

import event_catalog_service.dtos.req.CreateEventRequestDTO;
import event_catalog_service.dtos.req.SectorPricingRequestDTO;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Event Catalog Endpoint", description = "Gerenciamento do catálogo de eventos e configuração de setores/preços")
public interface EventCatalogContract {

    @Operation(summary = "Buscar os detalhes completos de um evento através do seu ID")
    ResponseEntity<EventDetailsResponseDTO> findEventById(
        @Parameter(description = "ID do evento")
        String id
    );

    @Operation(summary = "Registrar um novo evento no catálogo")
    ResponseEntity<EventDetailsResponseDTO> createEvent(
        CreateEventRequestDTO dto
    );

    @Operation(summary = "Adicionar um novo setor (e sua precificação) a um evento existente")
    ResponseEntity<SectorPricingResponseDTO> addSectorToAnEvent(
        @Parameter(description = "ID do evento")
        String id,

        SectorPricingRequestDTO dto
    );

    @Operation(summary = "Remover um setor específico de um evento")
    ResponseEntity<Void> removeSectorFromAnEvent(
        @Parameter(description = "ID do evento")
        String id,

        @Parameter(description = "ID do setor")
        String secId
    );
}