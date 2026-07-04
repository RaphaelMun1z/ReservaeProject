package ticket_service.entities;

import jakarta.persistence.*;
import ticket_service.entities.enums.TicketStatusEnum;

@Entity
@Table(name = "tb_tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String orderId;
    private String eventId;
    private String userId;
    private String sectorId;
    private String ticketId;
    private String qrCodeHash;

    @Enumerated(EnumType.STRING)
    private TicketStatusEnum status;

    public Ticket() {
    }

    public Ticket(
        String orderId,
        String eventId,
        String userId,
        String sectorId,
        String ticketId,
        String qrCodeHash,
        TicketStatusEnum status
    ) {
        this.orderId = orderId;
        this.eventId = eventId;
        this.userId = userId;
        this.sectorId = sectorId;
        this.ticketId = ticketId;
        this.qrCodeHash = qrCodeHash;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSectorId() {
        return sectorId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getQrCodeHash() {
        return qrCodeHash;
    }

    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
    }

    public TicketStatusEnum getStatus() {
        return status;
    }

    // Métodos Auxiliares
    public void revokeTicket() {
        this.status = TicketStatusEnum.REVOKED;
    }

    public void useTicket() {
        this.status = TicketStatusEnum.USED;
    }

    public void validateTicket() {
        this.status = TicketStatusEnum.VALID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return id != null && id.equals(ticket.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
