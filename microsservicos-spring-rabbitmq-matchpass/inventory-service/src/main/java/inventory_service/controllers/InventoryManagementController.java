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
@RequestMapping("/inventory-service/api/inventory")
public class InventoryManagementController implements InventoryManagementContract {
    private final InventoryManagementService inventoryManagementService;

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @Override
    @PostMapping(value = "/v1/create-tickets/event/{eventId}/sector/{sectorId}/amount/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketStatusResponseDTO>> createTickets(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int amount
    ) {
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
    @PostMapping(value = "/v1/reserve-ticket/{ticketId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketStatusResponseDTO> tryReserveTicket(
        @PathVariable String ticketId,
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(inventoryManagementService.tryReserveTicket(
            ticketId,
            userId
        ));
    }

    @Override
    @GetMapping(value = "/v1/consult-status/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketStatusResponseDTO> consultTicketStatus(@PathVariable String ticketId) {
        return ResponseEntity.ok(inventoryManagementService.consultTicketStatus(ticketId));
    }

    @Override
    @PatchMapping(value = "/v1/sold/{ticketId}")
    public ResponseEntity<Void> confirmTicketSold(@PathVariable String ticketId) {
        inventoryManagementService.confirmTicketSold(ticketId);
        return ResponseEntity.ok()
            .build();
    }

    @Override
    @PostMapping(value = "/v1/release/{ticketId}")
    public ResponseEntity<Void> releaseTicket(@PathVariable String ticketId) {
        inventoryManagementService.releaseTicket(ticketId);
        return ResponseEntity.ok()
            .build();
    }

    @Override
    @GetMapping(value = "/v1/user/{userId}/requested-tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketStatusResponseDTO>> findUserRequestedTickets(@PathVariable String userId) {
        return ResponseEntity.ok(inventoryManagementService.findUserRequestedTickets(userId));
    }

    @Override
    @GetMapping(value = "/v1/event/{eventId}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketResponseDTO>> findEventTicketsByStatus(
        @PathVariable String eventId,
        @RequestParam TicketStatusEnum status
    ) {
        return ResponseEntity.ok(inventoryManagementService.findEventTicketsByStatus(
            eventId,
            status
        ));
    }

    @Override
    @GetMapping(value = "/v1/ticket/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponseDTO> findTicketById(@PathVariable String ticketId) {
        return ResponseEntity.ok(inventoryManagementService.findTicketById(ticketId));
    }

    @Override
    @GetMapping(value = "/v1/event/{eventId}/sector/{sectorId}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TicketResponseDTO>> findEventSectorTicketsByStatus(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @RequestParam TicketStatusEnum status
    ) {
        return ResponseEntity.ok(inventoryManagementService.findEventSectorTicketsByStatus(
            eventId,
            sectorId,
            status
        ));
    }
}
