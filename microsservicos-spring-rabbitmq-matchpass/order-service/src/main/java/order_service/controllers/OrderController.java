package order_service.controllers;

import order_service.controllers.contracts.OrderContract;
import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.req.UpdateOrderStatusRequestDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class OrderController implements OrderContract {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO> checkout(
        CheckoutRequestDTO dto
    ) {
        OrderSummaryResponseDTO response =
            orderService.processCheckout(dto);

        URI location = URI.create(
            "/order-service/api/orders/" + response.orderId()
        );

        return ResponseEntity
            .accepted()
            .location(location)
            .body(response);
    }

    @Override
    public ResponseEntity<OrderResponseDTO> findProcessById(
        String orderId
    ) {
        return ResponseEntity.ok(
            orderService.findProcessById(orderId)
        );
    }

    @Override
    public ResponseEntity<List<OrderSummaryResponseDTO>>
    findProcessByEventId(String eventId) {
        return ResponseEntity.ok(
            orderService.findProcessByEventId(eventId)
        );
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO>
    findProcessBySeatTag(String seatTag) {
        return ResponseEntity.ok(
            orderService.findProcessBySeatTag(seatTag)
        );
    }

    @Override
    public ResponseEntity<OrderSummaryResponseDTO> updateProcessStatus(
        String orderId,
        UpdateOrderStatusRequestDTO request
    ) {
        return ResponseEntity.ok(
            orderService.updateProcessStatus(
                orderId,
                request.status()
            )
        );
    }

    @Override
    public ResponseEntity<List<OrderSummaryResponseDTO>>
    findOrdersByUserId(String userId) {
        return ResponseEntity.ok(
            orderService.findOrdersByUserId(userId)
        );
    }
}