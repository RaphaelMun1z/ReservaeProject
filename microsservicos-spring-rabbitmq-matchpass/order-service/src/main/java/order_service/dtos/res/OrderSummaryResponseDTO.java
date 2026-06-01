package order_service.dtos.res;

import order_service.entities.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderSummaryResponseDTO(
    String orderId,
    BigDecimal totalAmount,
    OrderStatus status,
    String paymentUrl
) {
}
