package ticket_service.dtos.req;

public record ValidateAccessRequestDTO(
    String qrCodeHash,
    String gateId
) {
}
