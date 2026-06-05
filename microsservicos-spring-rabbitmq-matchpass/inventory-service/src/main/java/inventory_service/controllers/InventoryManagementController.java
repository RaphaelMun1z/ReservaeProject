package inventory_service.controllers;

import inventory_service.controllers.contracts.InventoryManagementContract;
import inventory_service.dtos.req.SeatReservationRequestDTO;
import inventory_service.dtos.res.SeatResponseDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.enums.SeatStatusEnum;
import inventory_service.services.InventoryManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InventoryManagementController implements InventoryManagementContract {
    private final InventoryManagementService inventoryManagementService;

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @Override
    public ResponseEntity<List<SeatStatusResponseDTO>> createSeats(@PathVariable String eventId, @PathVariable String sectorId, @PathVariable int amount) {
        return ResponseEntity.ok(inventoryManagementService.createSeats(amount, new SeatReservationRequestDTO(eventId, sectorId, null)));
    }

    @Override
    public ResponseEntity<SeatStatusResponseDTO> tryLockSeat(@PathVariable String eventId, @PathVariable String sectorId, @PathVariable String seatTag, @PathVariable String userId) {
        SeatReservationRequestDTO dto = new SeatReservationRequestDTO(eventId, sectorId, seatTag);
        return ResponseEntity.ok(inventoryManagementService.tryLockSeat(dto, userId));
    }

    @Override
    public ResponseEntity<SeatStatusResponseDTO> checkSeatStatus(@PathVariable String seatTag) {
        return ResponseEntity.ok(inventoryManagementService.checkSeatStatus(seatTag));
    }

    @Override
    public ResponseEntity<Void> confirmSeatSold(@PathVariable String seatTag) {
        inventoryManagementService.confirmSeatSold(seatTag);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> releaseSeat(@PathVariable String seatTag) {
        inventoryManagementService.releaseSeat(seatTag);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<SeatStatusResponseDTO>> findUserSeats(@PathVariable String userId) {
        return ResponseEntity.ok(inventoryManagementService.findUserSeats(userId));
    }

    @Override
    public ResponseEntity<List<SeatResponseDTO>> findEventSeatsByStatus(@PathVariable String eventId, @RequestParam SeatStatusEnum status) {
        return ResponseEntity.ok(inventoryManagementService.findEventSeatsByStatus(eventId, status));
    }

    @Override
    public ResponseEntity<SeatResponseDTO> findSeatById(@PathVariable String seatId) {
        return ResponseEntity.ok(inventoryManagementService.findSeatById(seatId));
    }

    @Override
    public ResponseEntity<List<SeatResponseDTO>> findEventSectorSeatsByStatus(@PathVariable String eventId, @PathVariable String sectorId, @RequestParam SeatStatusEnum status) {
        return ResponseEntity.ok(inventoryManagementService.findEventSectorSeatsByStatus(eventId, sectorId, status));
    }
}
