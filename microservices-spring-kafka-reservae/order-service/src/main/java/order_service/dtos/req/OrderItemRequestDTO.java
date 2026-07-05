package order_service.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import order_service.entities.enums.TicketType;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
    @NotBlank(message = "O ID do setor é obrigatório.")
    String sectorId,

    @NotNull(message = "O tipo do ingresso é obrigatório.")
    TicketType ticketType,

    @Positive(message = "A quantidade deve ser maior que zero.")
    int quantity,

    @NotNull(message = "O preço aplicado é obrigatório.")
    @Positive(message = "O preço aplicado deve ser maior que zero.")
    BigDecimal appliedPrice
) {
}
