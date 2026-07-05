package ticket_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ticket_service.entities.Ticket;

import java.util.List;

@Tag(
    name = "Ticket Endpoint",
    description = "Consulta, acompanhamento e revogação de ingressos gerados"
)
@RequestMapping("/ticket-service/api/tickets")
public interface TicketContract {

    @Operation(summary = "Buscar os detalhes de um ingresso específico pelo ID")
    @GetMapping("/v1/{id}")
    ResponseEntity<Ticket> getTicketById(
        @PathVariable String id
    );

    @Operation(summary = "Listar todos os ingressos pertencentes a um usuário")
    @GetMapping("/v1/users/{userId}")
    ResponseEntity<List<Ticket>> getTicketsByUser(
        @PathVariable String userId
    );

    @Operation(summary = "Listar todos os ingressos de um evento com paginação")
    @GetMapping("/v1/events/{eventId}")
    ResponseEntity<Page<Ticket>> getTicketsByEvent(
        @PathVariable String eventId,
        Pageable pageable
    );

    @Operation(summary = "Revogar um ingresso específico")
    @PatchMapping("/v1/{id}/revoke")
    ResponseEntity<Void> revokeTicket(
        @PathVariable String id
    );
}