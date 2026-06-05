package order_service.controllers;

import order_service.controllers.contracts.OrderContract;
import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.entities.enums.OrderStatusEnum;
import order_service.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController implements OrderContract {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO> checkout(CheckoutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.processCheckout(dto));
    }

    @Override
    public ResponseEntity<OrderResponseDTO> findProcessById(String processId) {
        return ResponseEntity.ok(orderService.findProcessById(processId));
    }

    @Override
    public ResponseEntity<List<OrderSummaryResponseDTO>> findProcessByEventId(String eventId) {
        return ResponseEntity.ok(orderService.findProcessByEventId(eventId));
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO> findProcessBySeatTag(String seatTag) {
        return ResponseEntity.ok(orderService.findProcessBySeatTag(seatTag));
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO> updateProcessStatus(String orderId, OrderStatusEnum status) {
        return ResponseEntity.ok(orderService.updateProcessStatus(orderId, status));
    }

    @Override
    public ResponseEntity<List<OrderSummaryResponseDTO>> findOrdersByUserId(String userId) {
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }
}