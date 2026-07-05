package order_service.dtos.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CheckoutRequestDTO(
    @NotBlank(message = "O ID do usuário é obrigatório.")
    String userId,

    @NotBlank(message = "O ID do evento é obrigatório.")
    String eventId,

    @NotEmpty(message = "O pedido deve possuir ao menos um item.")
    List<@Valid OrderItemRequestDTO> items
) {
}
