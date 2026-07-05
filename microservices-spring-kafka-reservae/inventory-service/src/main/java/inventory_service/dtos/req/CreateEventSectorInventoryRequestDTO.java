package inventory_service.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateEventSectorInventoryRequestDTO(
    @NotBlank(message = "O ID do evento é obrigatório.")
    String eventId,

    @NotBlank(message = "O ID do setor é obrigatório.")
    String sectorId,

    @Positive(message = "A capacidade do setor deve ser maior que zero.")
    int capacity
) {
}