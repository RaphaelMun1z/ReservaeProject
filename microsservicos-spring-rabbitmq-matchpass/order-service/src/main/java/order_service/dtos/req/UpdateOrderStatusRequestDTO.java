package order_service.dtos.req;

import jakarta.validation.constraints.NotNull;
import order_service.entities.enums.OrderStatusEnum;

public record UpdateOrderStatusRequestDTO(
    @NotNull
    OrderStatusEnum status
) {
}