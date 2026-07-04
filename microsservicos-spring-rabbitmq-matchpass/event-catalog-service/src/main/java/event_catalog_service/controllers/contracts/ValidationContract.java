package event_catalog_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Validation Endpoint", description = "Endpoints de serviço para validação de integridade de eventos, setores e assentos")
@RequestMapping("/event-catalog-service/api/event/validate")
public interface ValidationContract {

    @Operation(summary = "Verificar se um evento específico existe no catálogo")
    @GetMapping("/v1/{eventId}/exists")
    ResponseEntity<String> validateEvent(@PathVariable String eventId);

    @Operation(summary = "Verificar se um setor específico existe dentro de um evento")
    @GetMapping("/v1/{eventId}/sector/{sectorId}/exists")
    ResponseEntity<String> validateEventSector(
            @PathVariable String eventId,
            @PathVariable String sectorId
    );

    @Operation(summary = "Validar a criação de assentos (ex: checar limites de capacidade do setor)")
    @GetMapping("/v1/{eventId}/sector/{sectorId}/tickets/{ticketsAmount}")
    ResponseEntity<String> validateEventSectorTicketCreating(
            @PathVariable String eventId,
            @PathVariable String sectorId,
            @PathVariable int ticketsAmount
    );
}