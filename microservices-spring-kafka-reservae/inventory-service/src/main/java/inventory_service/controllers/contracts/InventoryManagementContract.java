package inventory_service.controllers.contracts;

import inventory_service.dtos.req.CreateEventSectorInventoryRequestDTO;
import inventory_service.dtos.req.CreateTicketReservationRequestDTO;
import inventory_service.dtos.res.EventSectorInventoryResponseDTO;
import inventory_service.dtos.res.ReservationStatusResponseDTO;
import inventory_service.dtos.res.TicketReservationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Inventory Endpoint",
    description = "Controle de inventário, disponibilidade e reservas temporárias de ingressos"
)
public interface InventoryManagementContract {

    @Operation(summary = "Criar inventário de ingressos para um evento e setor")
    ResponseEntity<EventSectorInventoryResponseDTO> createEventSectorInventory(
        @RequestBody @Valid CreateEventSectorInventoryRequestDTO request
    );

    @Operation(summary = "Consultar inventário de ingressos de um evento e setor")
    ResponseEntity<EventSectorInventoryResponseDTO> findInventoryByEventAndSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    );

    @Operation(summary = "Reservar temporariamente uma quantidade de ingressos")
    ResponseEntity<TicketReservationResponseDTO> reserveTickets(
        @RequestBody @Valid CreateTicketReservationRequestDTO request
    );

    @Operation(summary = "Consultar o status de uma reserva")
    ResponseEntity<ReservationStatusResponseDTO> consultReservationStatus(
        @PathVariable String reservationId
    );

    @Operation(summary = "Confirmar uma reserva como venda")
    ResponseEntity<Void> confirmReservationSale(
        @PathVariable String reservationId
    );

    @Operation(summary = "Liberar uma reserva temporária")
    ResponseEntity<Void> releaseReservation(
        @PathVariable String reservationId
    );

    @Operation(summary = "Confirmar todas as reservas de um pedido como venda")
    ResponseEntity<Void> confirmOrderReservations(
        @PathVariable String orderId
    );

    @Operation(summary = "Liberar todas as reservas de um pedido")
    ResponseEntity<Void> releaseOrderReservations(
        @PathVariable String orderId
    );

    @Operation(summary = "Buscar reservas de um usuário")
    ResponseEntity<List<ReservationStatusResponseDTO>> findUserReservations(
        @PathVariable String userId
    );

    @Operation(summary = "Buscar reservas vinculadas a um pedido")
    ResponseEntity<List<TicketReservationResponseDTO>> findOrderReservations(
        @PathVariable String orderId
    );

    @Operation(summary = "Buscar detalhes de uma reserva pelo ID")
    ResponseEntity<TicketReservationResponseDTO> findReservationById(
        @PathVariable String reservationId
    );
}