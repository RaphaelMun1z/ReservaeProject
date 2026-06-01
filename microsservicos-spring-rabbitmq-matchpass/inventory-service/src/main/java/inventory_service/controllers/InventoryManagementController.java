package inventory_service.controllers;

import inventory_service.dtos.req.SeatReservationRequestDTO;
import inventory_service.dtos.res.SeatResponseDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.enums.SeatStatusEnum;
import inventory_service.environment.InstanceInformationService;
import inventory_service.services.InventoryManagementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inventory")
public class InventoryManagementController {
    private final InstanceInformationService informationService;
    private final InventoryManagementService inventoryManagementService;

    public InventoryManagementController(InstanceInformationService informationService, InventoryManagementService inventoryManagementService) {
        this.informationService = informationService;
        this.inventoryManagementService = inventoryManagementService;
    }

    @PostMapping(
        value = "/create-seats/event/{eventId}/sector/{sectorId}/amount/{amount}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<SeatStatusResponseDTO>> createSeats(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int amount) {
        return ResponseEntity.ok(inventoryManagementService.createSeats(
            amount,
            new SeatReservationRequestDTO(
                eventId,
                sectorId,
                null
            )
        ));
    }

    @PostMapping(
        value = "/lock-seat/event/{eventId}/sector/{sectorId}/seat/{seatTag}/user/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SeatStatusResponseDTO> tryLockSeat(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable String seatTag,
        @PathVariable String userId) {
        SeatReservationRequestDTO dto = new SeatReservationRequestDTO(
            eventId,
            sectorId,
            seatTag
        );
        return ResponseEntity.ok(inventoryManagementService.tryLockSeat(
            dto,
            userId,
            "PORT " + informationService.retrieveServerPort()
        ));
    }

    @GetMapping(
        value = "/check/{seatTag}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SeatStatusResponseDTO> checkSeatStatus(@PathVariable String seatTag) {
        return ResponseEntity.ok(inventoryManagementService.checkSeatStatus(
            seatTag,
            "PORT " + informationService.retrieveServerPort()
        ));
    }

    @PatchMapping("/confirm/{seatTag}")
    public ResponseEntity<Void> confirmSeatSold(@PathVariable String seatTag) {
        inventoryManagementService.confirmSeatSold(seatTag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release/{seatTag}")
    public ResponseEntity<Void> releaseSeat(@PathVariable String seatTag) {
        inventoryManagementService.releaseSeat(seatTag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<SeatStatusResponseDTO>> findUserSeats(@PathVariable String userId) {
        return ResponseEntity.ok(inventoryManagementService.findUserSeats(
            userId,
            "PORT " + informationService.retrieveServerPort()
        ));
    }

    @GetMapping("event/{eventId}/seat")
    public ResponseEntity<List<SeatResponseDTO>> findEventSeatsByStatus(
        @PathVariable String eventId,
        @RequestParam SeatStatusEnum status
    ) {
        return ResponseEntity.ok(inventoryManagementService.findEventSeatsByStatus(
            eventId,
            status,
            "PORT " + informationService.retrieveServerPort())
        );
    }

    @GetMapping("seat/{seatId}")
    public ResponseEntity<SeatResponseDTO> findSeatById(@PathVariable String seatId) {
        return ResponseEntity.ok(inventoryManagementService.findSeatById(
            seatId,
            "PORT " + informationService.retrieveServerPort())
        );
    }

    @GetMapping("event/{eventId}/sector/{sectorId}/seats")
    public ResponseEntity<List<SeatResponseDTO>> findEventSectorSeatsByStatus(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @RequestParam SeatStatusEnum status) {
        return ResponseEntity.ok(inventoryManagementService.findEventSectorSeatsByStatus(
            eventId,
            sectorId,
            status,
            "PORT " + informationService.retrieveServerPort()
        ));
    }
}
