package event_catalog_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Validation Endpoint", description = "Endpoints de serviço para validação de integridade de eventos, setores e assentos")
public interface ValidationContract {

    @Operation(summary = "Verificar se um evento específico existe no catálogo")
    ResponseEntity<String> validateEvent(String eventId);

    @Operation(summary = "Verificar se um setor específico existe dentro de um evento")
    ResponseEntity<String> validateEventSector(
        String eventId,
        String sectorId
    );

    @Operation(summary = "Validar a criação de assentos (ex: checar limites de capacidade do setor)")
    ResponseEntity<String> validateSectorCapacity(
        String eventId,
        String sectorId,
        int ticketsAmount
    );
}