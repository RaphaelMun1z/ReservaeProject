package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.TeamContract;
import event_catalog_service.dtos.req.TeamRequestDTO;
import event_catalog_service.dtos.res.TeamResponseDTO;
import event_catalog_service.services.TeamService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController implements TeamContract {
    private final TeamService teamService;

    @Value("${event-catalog-service.teste:valor-padrao}")
    private String teste;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public ResponseEntity<TeamResponseDTO> createTeam(TeamRequestDTO dto) {
        return ResponseEntity.ok().body(teamService.createTeam(dto));
    }

    @Override
    public ResponseEntity<String> getTestConfig() {
        return ResponseEntity.ok("O valor da variável é: " + teste);
    }
}
