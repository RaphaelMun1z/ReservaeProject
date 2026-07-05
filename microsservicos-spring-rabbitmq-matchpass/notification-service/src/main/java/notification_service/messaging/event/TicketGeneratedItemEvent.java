package notification_service.messaging.event;

public record TicketGeneratedItemEvent(
    String ticketId,
    String sectorId,
    String ticketType,
    String qrCodeHash
) {
}