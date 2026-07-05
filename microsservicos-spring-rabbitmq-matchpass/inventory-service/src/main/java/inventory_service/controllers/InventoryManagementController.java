package inventory_service.controllers;

import inventory_service.controllers.contracts.InventoryManagementContract;
import inventory_service.dtos.req.CreateEventSectorInventoryRequestDTO;
import inventory_service.dtos.req.CreateTicketReservationRequestDTO;
import inventory_service.dtos.res.EventSectorInventoryResponseDTO;
import inventory_service.dtos.res.ReservationStatusResponseDTO;
import inventory_service.dtos.res.TicketReservationResponseDTO;
import inventory_service.services.InventoryManagementService;
import jakarta.validation.Valid;
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
    @PostMapping(
        value = "/v1/event-sector",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EventSectorInventoryResponseDTO> createEventSectorInventory(
        @RequestBody @Valid CreateEventSectorInventoryRequestDTO request
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.createEventSectorInventory(
                request.eventId(),
                request.sectorId(),
                request.capacity()
            )
        );
    }

    @Override
    @GetMapping(
        value = "/v1/event/{eventId}/sector/{sectorId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EventSectorInventoryResponseDTO> findInventoryByEventAndSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.findInventoryByEventAndSector(
                eventId,
                sectorId
            )
        );
    }

    @Override
    @PostMapping(
        value = "/v1/reservations",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketReservationResponseDTO> reserveTickets(
        @RequestBody @Valid CreateTicketReservationRequestDTO request
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.reserveTickets(
                request.orderId(),
                request.userId(),
                request.eventId(),
                request.sectorId(),
                request.quantity()
            )
        );
    }

    @Override
    @GetMapping(
        value = "/v1/reservations/{reservationId}/status",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReservationStatusResponseDTO> consultReservationStatus(
        @PathVariable String reservationId
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.consultReservationStatus(reservationId)
        );
    }

    @Override
    @PostMapping(value = "/v1/reservations/{reservationId}/confirm-sale")
    public ResponseEntity<Void> confirmReservationSale(
        @PathVariable String reservationId
    ) {
        inventoryManagementService.confirmReservationSale(reservationId);

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping(value = "/v1/reservations/{reservationId}/release")
    public ResponseEntity<Void> releaseReservation(
        @PathVariable String reservationId
    ) {
        inventoryManagementService.releaseReservation(reservationId);

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping(value = "/v1/orders/{orderId}/confirm-sale")
    public ResponseEntity<Void> confirmOrderReservations(
        @PathVariable String orderId
    ) {
        inventoryManagementService.confirmOrderReservations(orderId);

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping(value = "/v1/orders/{orderId}/release")
    public ResponseEntity<Void> releaseOrderReservations(
        @PathVariable String orderId
    ) {
        inventoryManagementService.releaseOrderReservations(orderId);

        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(
        value = "/v1/users/{userId}/reservations",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ReservationStatusResponseDTO>> findUserReservations(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.findUserReservations(userId)
        );
    }

    @Override
    @GetMapping(
        value = "/v1/orders/{orderId}/reservations",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketReservationResponseDTO>> findOrderReservations(
        @PathVariable String orderId
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.findOrderReservations(orderId)
        );
    }

    @Override
    @GetMapping(
        value = "/v1/reservations/{reservationId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketReservationResponseDTO> findReservationById(
        @PathVariable String reservationId
    ) {
        return ResponseEntity.ok(
            inventoryManagementService.findReservationById(reservationId)
        );
    }
}