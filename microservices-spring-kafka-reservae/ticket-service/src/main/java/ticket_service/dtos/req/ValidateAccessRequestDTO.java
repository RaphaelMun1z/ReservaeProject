package ticket_service.dtos.req;

import jakarta.validation.constraints.NotBlank;

public record ValidateAccessRequestDTO(
    @NotBlank(message = "O hash do QR Code é obrigatório.")
    String qrCodeHash,

    @NotBlank(message = "O ID do portão é obrigatório.")
    String gateId
) {
}