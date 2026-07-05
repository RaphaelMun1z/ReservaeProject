package ticket_service.entities;

import jakarta.persistence.*;
import ticket_service.entities.enums.AccessStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "gate_id", nullable = false)
    private String gateId;

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private AccessStatusEnum result;

    protected AccessLog() {
    }

    public AccessLog(
        String ticketId,
        String eventId,
        String gateId,
        LocalDateTime accessedAt,
        AccessStatusEnum result
    ) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.gateId = gateId;
        this.accessedAt = accessedAt;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getGateId() {
        return gateId;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public AccessStatusEnum getResult() {
        return result;
    }
}