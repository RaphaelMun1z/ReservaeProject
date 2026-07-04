package order_service.controllers;

import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service/api/orders")
public class OrderPaymentController {
    private final OrderService orderService;

    public OrderPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<OrderSummaryResponseDTO> requestPayment(
            @PathVariable String orderId
    ) {
        OrderSummaryResponseDTO response = orderService.requestPayment(orderId);
        return ResponseEntity.accepted().body(response);
    }
}