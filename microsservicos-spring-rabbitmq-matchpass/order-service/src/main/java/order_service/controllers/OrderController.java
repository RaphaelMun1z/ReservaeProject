package order_service.controllers;

import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.entities.enums.OrderStatusEnum;
import order_service.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderSummaryResponseDTO> checkout(
        @RequestBody CheckoutRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.processCheckout(dto));
    }

    @GetMapping("/{processId}")
    public ResponseEntity<OrderResponseDTO> findProcessById(
        @PathVariable String processId
    ) {
        return ResponseEntity.ok(orderService.findProcessById(processId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<OrderSummaryResponseDTO>> findProcessByEventId(
        @PathVariable String eventId
    ) {
        return ResponseEntity.ok(orderService.findProcessByEventId(eventId));
    }

    @GetMapping("/seat/{seatTag}")
    public ResponseEntity<OrderSummaryResponseDTO> findProcessBySeatTag(
        @PathVariable String seatTag
    ) {
        return ResponseEntity.ok(orderService.findProcessBySeatTag(seatTag));
    }

    @PatchMapping("/checkout/{orderId}")
    public ResponseEntity<OrderSummaryResponseDTO> updateProcessStatus(
        @PathVariable String orderId,
        @RequestParam OrderStatusEnum status
    ) {
        return ResponseEntity.ok(orderService.updateProcessStatus(orderId, status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryResponseDTO>> findOrdersByUserId(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }
}
