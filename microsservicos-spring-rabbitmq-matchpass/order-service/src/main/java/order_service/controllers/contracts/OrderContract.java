package order_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.entities.enums.OrderStatusEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Endpoint", description = "Gerenciamento do ciclo de vida de pedidos, checkout e acompanhamento de status")
@RequestMapping("/order-service/api/order")
public interface OrderContract {

    @Operation(summary = "Processar um novo checkout de pedido")
    @PostMapping("/checkout")
    ResponseEntity<OrderSummaryResponseDTO> checkout(@RequestBody CheckoutRequestDTO dto);

    @Operation(summary = "Buscar os detalhes completos de um processo/pedido pelo seu ID")
    @GetMapping("/{processId}")
    ResponseEntity<OrderResponseDTO> findProcessById(@PathVariable String processId);

    @Operation(summary = "Listar todos os pedidos associados a um evento específico")
    @GetMapping("/event/{eventId}")
    ResponseEntity<List<OrderSummaryResponseDTO>> findProcessByEventId(@PathVariable String eventId);

    @Operation(summary = "Buscar um pedido através da tag/código do assento")
    @GetMapping("/seat/{seatTag}")
    ResponseEntity<OrderSummaryResponseDTO> findProcessBySeatTag(@PathVariable String seatTag);

    @Operation(summary = "Atualizar o status de um pedido existente")
    @PatchMapping("/{orderId}/status")
    ResponseEntity<OrderSummaryResponseDTO> updateProcessStatus(
        @PathVariable String orderId,
        @RequestBody OrderStatusEnum status
    );

    @Operation(summary = "Listar todos os pedidos realizados por um usuário específico")
    @GetMapping("/user/{userId}")
    ResponseEntity<List<OrderSummaryResponseDTO>> findOrdersByUserId(@PathVariable String userId);
}