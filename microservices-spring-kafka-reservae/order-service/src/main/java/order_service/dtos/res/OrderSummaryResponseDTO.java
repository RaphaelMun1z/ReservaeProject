package order_service.dtos.res;

import order_service.entities.enums.OrderStatusEnum;

import java.math.BigDecimal;

public record OrderSummaryResponseDTO(
    String orderId,
    BigDecimal totalAmount,
    OrderStatusEnum status,
    String paymentUrl
) {
}
