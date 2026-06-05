package event_catalog_service.controllers.contracts;

import event_catalog_service.dtos.req.TeamRequestDTO;
import event_catalog_service.dtos.res.TeamResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Team Endpoint", description = "Gerenciamento de times no catálogo de eventos")
@RequestMapping("/event-catalog-service/api/team")
public interface TeamContract {

    @Operation(summary = "Criar um novo time")
    @PostMapping("/v1")
    ResponseEntity<TeamResponseDTO> createTeam(@RequestBody TeamRequestDTO dto);

    @Operation(summary = "Endpoint de teste para verificar injeção de propriedades de configuração")
    @GetMapping("/test-config")
    ResponseEntity<String> getTestConfig();
}