package order_service.dtos.req;

import java.util.List;

public record CheckoutRequestDTO(
    String eventId,
    List<OrderItemRequestDTO> items
) {
}
