package order_service.dtos.res;

import order_service.entities.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDTO(
    String orderId,
    BigDecimal totalAmount,
    OrderStatusEnum status,
    String paymentUrl,
    List<OrderItemResponseDTO> itens
) {
}
