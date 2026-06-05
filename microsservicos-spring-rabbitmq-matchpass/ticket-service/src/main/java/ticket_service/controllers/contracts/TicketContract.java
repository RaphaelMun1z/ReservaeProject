package ticket_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket_service.dtos.req.GenerateTicketRequestDTO;
import ticket_service.entities.Ticket;

import java.util.List;

@Tag(name = "Ticket Endpoint", description = "Gerenciamento do ciclo de vida dos ingressos, incluindo emissão, consultas e revogação")
@RequestMapping("/ticket-service/api/ticket")
public interface TicketContract {

    @Operation(summary = "Gerar novos ingressos a partir de um pedido (order) finalizado")
    @PostMapping("/v1")
    ResponseEntity<List<Ticket>> generateTickets(@RequestBody GenerateTicketRequestDTO dto);

    @Operation(summary = "Buscar os detalhes de um ingresso específico pelo seu ID")
    @GetMapping("/v1/{id}")
    ResponseEntity<Ticket> getTicketById(@PathVariable String id);

    @Operation(summary = "Listar todos os ingressos pertencentes a um usuário específico")
    @GetMapping("/v1/user/{userId}")
    ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable String userId);

    @Operation(summary = "Listar todos os ingressos de um evento com suporte a paginação")
    @GetMapping("/v1/event/{eventId}")
    ResponseEntity<Page<Ticket>> getTicketsByEvent(
        @PathVariable String eventId,
        Pageable pageable
    );

    @Operation(summary = "Revogar (cancelar) um ingresso específico")
    @PatchMapping("/v1/{id}/revoke")
    ResponseEntity<Void> revokeTicket(@PathVariable String id);
}