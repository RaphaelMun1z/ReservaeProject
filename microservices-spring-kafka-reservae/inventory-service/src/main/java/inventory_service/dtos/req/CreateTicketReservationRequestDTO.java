package inventory_service.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateTicketReservationRequestDTO(
    @NotBlank(message = "O ID do pedido é obrigatório.")
    String orderId,

    @NotBlank(message = "O ID do usuário é obrigatório.")
    String userId,

    @NotBlank(message = "O ID do evento é obrigatório.")
    String eventId,

    @NotBlank(message = "O ID do setor é obrigatório.")
    String sectorId,

    @Positive(message = "A quantidade de ingressos deve ser maior que zero.")
    int quantity
) {
}