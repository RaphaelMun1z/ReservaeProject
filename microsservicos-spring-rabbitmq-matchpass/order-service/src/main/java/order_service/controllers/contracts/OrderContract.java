package order_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.req.UpdateOrderStatusRequestDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Order Endpoint",
        description = "Gerenciamento do ciclo de vida de pedidos, checkout e acompanhamento de status"
)
@RequestMapping("/order-service/api/orders")
public interface OrderContract {

    @Operation(
            summary = "Criar um novo pedido",
            description = "Cria o pedido e envia uma solicitação assíncrona para reservar os ingressos no Inventory Service."
    )
    @PostMapping("/checkout")
    ResponseEntity<OrderSummaryResponseDTO> checkout(
            @Valid @RequestBody CheckoutRequestDTO dto
    );

    @Operation(summary = "Buscar os detalhes completos de um pedido pelo ID")
    @GetMapping("/{orderId}")
    ResponseEntity<OrderResponseDTO> findProcessById(
            @PathVariable String orderId
    );

    @Operation(summary = "Listar todos os pedidos associados a um evento")
    @GetMapping("/event/{eventId}")
    ResponseEntity<List<OrderSummaryResponseDTO>> findProcessByEventId(
            @PathVariable String eventId
    );

    @Operation(summary = "Buscar um pedido pela tag do assento")
    @GetMapping("/ticket/{ticketTag}")
    ResponseEntity<OrderSummaryResponseDTO> findProcessByTicketTag(
            @PathVariable String ticketTag
    );

    @Operation(summary = "Atualizar o status de um pedido")
    @PatchMapping("/{orderId}/status")
    ResponseEntity<OrderSummaryResponseDTO> updateProcessStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request
    );

    @Operation(summary = "Listar todos os pedidos de um usuário")
    @GetMapping("/user/{userId}")
    ResponseEntity<List<OrderSummaryResponseDTO>> findOrdersByUserId(
            @PathVariable String userId
    );
}