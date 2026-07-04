package ticket_service.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ticket_service.controllers.contracts.TicketContract;
import ticket_service.dtos.req.GenerateTicketRequestDTO;
import ticket_service.entities.Ticket;
import ticket_service.services.TicketService;

import java.util.List;

@RestController
public class TicketController implements TicketContract {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public ResponseEntity<List<Ticket>> generateTickets(GenerateTicketRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.generateFromOrder(dto));
    }

    @Override
    public ResponseEntity<Ticket> getTicketById(String id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @Override
    public ResponseEntity<List<Ticket>> getTicketsByUser(String userId) {
        return ResponseEntity.ok(ticketService.findByUserId(userId));
    }

    @Override
    public ResponseEntity<Page<Ticket>> getTicketsByEvent(String eventId, Pageable pageable) {
        return ResponseEntity.ok(ticketService.findByEventId(
            eventId,
            pageable
        ));
    }

    @Override
    public ResponseEntity<Void> revokeTicket(String id) {
        ticketService.revokeTicket(id);
        return ResponseEntity.noContent().build();
    }
}