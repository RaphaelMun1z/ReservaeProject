package event_catalog_service.controllers;

import event_catalog_service.dtos.req.TeamRequestDTO;
import event_catalog_service.dtos.res.TeamResponseDTO;
import event_catalog_service.services.TeamService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    @Value("${event-catalog-service.teste:valor-padrao}")
    private String teste;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/v1")
    public ResponseEntity<TeamResponseDTO> createTeam(@RequestBody TeamRequestDTO dto) {
        return ResponseEntity.ok().body(teamService.createTeam(dto));
    }

    @GetMapping("/test-config")
    public ResponseEntity<String> getTestConfig() {
        return ResponseEntity.ok("O valor da variável é: " + teste);
    }
}
