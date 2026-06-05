package inventory_service.controllers.contracts;

import inventory_service.dtos.res.SeatResponseDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.enums.SeatStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory Endpoint", description = "Controle e gerenciamento do inventário de assentos em tempo real")
@RequestMapping("/inventory-service/api/inventory")
public interface InventoryManagementContract {
    @Operation(summary = "Criar novos assentos para um evento e setor específicos")
    @PostMapping(
        value = "/create-seats/event/{eventId}/sector/{sectorId}/amount/{amount}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<SeatStatusResponseDTO>> createSeats(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int amount
    );

    @Operation(summary = "Bloquear temporariamente um assento para um usuário")
    @PostMapping(
        value = "/lock-seat/event/{eventId}/sector/{sectorId}/seat/{seatTag}/user/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SeatStatusResponseDTO> tryLockSeat(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable String seatTag,
        @PathVariable String userId
    );

    @Operation(summary = "Verificar o status atual de um assento específico")
    @GetMapping(
        value = "/check/{seatTag}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SeatStatusResponseDTO> checkSeatStatus(@PathVariable String seatTag);

    @Operation(summary = "Confirmar a venda de um assento bloqueado")
    @PatchMapping("/confirm/{seatTag}")
    ResponseEntity<Void> confirmSeatSold(@PathVariable String seatTag);

    @Operation(summary = "Liberar um assento bloqueado de volta para o inventário")
    @PostMapping("/release/{seatTag}")
    ResponseEntity<Void> releaseSeat(@PathVariable String seatTag);

    @Operation(summary = "Buscar todos os assentos associados a um usuário")
    @GetMapping(
        value = "user/{userId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<SeatStatusResponseDTO>> findUserSeats(@PathVariable String userId);

    @Operation(summary = "Listar assentos de um evento filtrados por status")
    @GetMapping(
        value = "event/{eventId}/seat",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<SeatResponseDTO>> findEventSeatsByStatus(
        @PathVariable String eventId,
        @RequestParam SeatStatusEnum status
    );

    @Operation(summary = "Buscar detalhes de um assento pelo seu ID único")
    @GetMapping(
        value = "seat/{seatId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SeatResponseDTO> findSeatById(@PathVariable String seatId);

    @Operation(summary = "Listar assentos de um setor específico de um evento filtrados por status")
    @GetMapping(
        value = "event/{eventId}/sector/{sectorId}/seats",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<SeatResponseDTO>> findEventSectorSeatsByStatus(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @RequestParam SeatStatusEnum status
    );
}
