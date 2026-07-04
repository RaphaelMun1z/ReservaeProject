package inventory_service.controllers;

import inventory_service.controllers.contracts.InventoryManagementContract;
import inventory_service.dtos.req.TicketReservationRequestDTO;
import inventory_service.dtos.res.TicketResponseDTO;
import inventory_service.dtos.res.TicketStatusResponseDTO;
import inventory_service.entities.enums.TicketStatusEnum;
import inventory_service.services.InventoryManagementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryManagementController implements InventoryManagementContract {
    private final InventoryManagementService inventoryManagementService;

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @Override
    @PostMapping(value = "/create-tickets/event/{eventId}/sector/{sectorId}/amount/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketStatusResponseDTO>> createTickets(@PathVariable String eventId, @PathVariable String sectorId, @PathVariable int amount) {
        return ResponseEntity.ok(inventoryManagementService.createTickets(
                amount,
                new TicketReservationRequestDTO(
                        eventId,
                        sectorId,
                        null
                )
        ));
    }

    @Override
    @PostMapping(value = "/reserve-ticket/{ticketTag}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketStatusResponseDTO> tryReserveTicket(@PathVariable String ticketTag, @PathVariable String userId) {
        return ResponseEntity.ok(inventoryManagementService.tryReserveTicket(
                ticketTag,
                userId
        ));
    }

    @Override
    @GetMapping(value = "/check/{ticketTag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketStatusResponseDTO> checkTicketStatus(@PathVariable String ticketTag) {
        return ResponseEntity.ok(inventoryManagementService.checkTicketStatus(ticketTag));
    }

    @Override
    @PatchMapping("/confirm/{ticketTag}")
    public ResponseEntity<Void> confirmTicketSold(@PathVariable String ticketTag) {
        inventoryManagementService.confirmTicketSold(ticketTag);
        return ResponseEntity.ok()
                .build();
    }

    @Override
    @PostMapping("/release/{ticketTag}")
    public ResponseEntity<Void> releaseTicket(@PathVariable String ticketTag) {
        inventoryManagementService.releaseTicket(ticketTag);
        return ResponseEntity.ok()
                .build();
    }

    @Override
    @GetMapping(value = "user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketStatusResponseDTO>> findUserTickets(@PathVariable String userId) {
        return ResponseEntity.ok(inventoryManagementService.findUserTickets(userId));
    }

    @Override
    @GetMapping(value = "event/{eventId}/ticket", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketResponseDTO>> findEventTicketsByStatus(@PathVariable String eventId, @RequestParam TicketStatusEnum status) {
        return ResponseEntity.ok(inventoryManagementService.findEventTicketsByStatus(
                eventId,
                status
        ));
    }

    @Override
    @GetMapping(value = "ticket/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponseDTO> findTicketById(@PathVariable String ticketId) {
        return ResponseEntity.ok(inventoryManagementService.findTicketById(ticketId));
    }

    @Override
    @GetMapping(value = "event/{eventId}/sector/{sectorId}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketResponseDTO>> findEventSectorTicketsByStatus(@PathVariable String eventId, @PathVariable String sectorId, @RequestParam TicketStatusEnum status) {
        return ResponseEntity.ok(inventoryManagementService.findEventSectorTicketsByStatus(
                eventId,
                sectorId,
                status
        ));
    }
}
