package inventory_service.controllers.contracts;

import inventory_service.dtos.res.TicketResponseDTO;
import inventory_service.dtos.res.TicketStatusResponseDTO;
import inventory_service.entities.enums.TicketStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Inventory Endpoint", description = "Controle e gerenciamento do inventário de assentos em tempo real")
public interface InventoryManagementContract {
    @Operation(summary = "Criar novos assentos para um evento e setor específicos")
    ResponseEntity<List<TicketStatusResponseDTO>> createTickets(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int amount
    );

    @Operation(summary = "Bloquear temporariamente um assento para um usuário")
    ResponseEntity<TicketStatusResponseDTO> tryReserveTicket(
        @PathVariable String ticketId,
        @PathVariable String userId
    );

    @Operation(summary = "Verificar o status atual de um assento específico")
    ResponseEntity<TicketStatusResponseDTO> consultTicketStatus(@PathVariable String ticketId);

    @Operation(summary = "Confirmar a venda de um assento bloqueado")
    ResponseEntity<Void> confirmTicketSold(@PathVariable String ticketId);

    @Operation(summary = "Liberar um assento bloqueado de volta para o inventário")
    ResponseEntity<Void> releaseTicket(@PathVariable String ticketId);

    @Operation(summary = "Buscar todos os assentos associados a um usuário")
    ResponseEntity<List<TicketStatusResponseDTO>> findUserRequestedTickets(@PathVariable String userId);

    @Operation(summary = "Listar assentos de um evento filtrados por status")
    ResponseEntity<List<TicketResponseDTO>> findEventTicketsByStatus(
        @PathVariable String eventId,
        @RequestParam TicketStatusEnum status
    );

    @Operation(summary = "Buscar detalhes de um assento pelo seu ID único")
    ResponseEntity<TicketResponseDTO> findTicketById(@PathVariable String ticketId);

    @Operation(summary = "Listar assentos de um setor específico de um evento filtrados por status")
    ResponseEntity<List<TicketResponseDTO>> findEventSectorTicketsByStatus(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @RequestParam TicketStatusEnum status
    );
}
