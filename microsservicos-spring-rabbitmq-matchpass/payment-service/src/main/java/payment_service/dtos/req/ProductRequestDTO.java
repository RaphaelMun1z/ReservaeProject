package payment_service.dtos.req;

import jakarta.validation.constraints.*;

public record ProductRequestDTO(
    @NotBlank(message = "O identificador do pedido é obrigatório.")
    String orderId,

    @NotBlank(message = "O identificador do usuário é obrigatório.")
    String userId,

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser maior que zero.")
    Long amount,

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1.")
    Long quantity,

    @NotBlank(message = "O nome do produto é obrigatório.")
    String name,

    @NotBlank(message = "A moeda é obrigatória.")
    String currency,

    @Email(message = "O e-mail do cliente deve ser válido.")
    String customerEmail
) {
}