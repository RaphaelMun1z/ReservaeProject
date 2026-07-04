package inventory_service.entities;

import inventory_service.entities.enums.TicketStatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash("TicketReserve")
public class TicketReserve {
    private static final long LOCK_TTL_SECONDS = 600L;
    @Id
    private String ticketId;
    @Indexed
    private String eventId;
    @Indexed
    private String sectorId;
    @Indexed
    private String userId;
    @Indexed
    private TicketStatusEnum ticketStatus;
    @TimeToLive
    private Long timeToLive;

    public TicketReserve() {
    }

    public TicketReserve(String eventId, String sectorId, String userId, TicketStatusEnum ticketStatus) {
        this.ticketId = UUID.randomUUID().toString();
        this.eventId = eventId;
        this.sectorId = sectorId;
        this.userId = userId;
        this.ticketStatus = ticketStatus;
        this.timeToLive = (long) -1;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSectorId() {
        return sectorId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public TicketStatusEnum getTicketStatus() {
        return ticketStatus;
    }

    public Long getTimeToLive() {
        return timeToLive;
    }

    // Métodos auxiliares
    public void reserve(String userId) {
        this.userId = userId;
        this.ticketStatus = TicketStatusEnum.RESERVED;
        this.timeToLive = LOCK_TTL_SECONDS;
    }

    public void release() {
        this.ticketStatus = TicketStatusEnum.AVAILABLE;
        this.timeToLive = (long) -1;
    }

    public void sold() {
        this.ticketStatus = TicketStatusEnum.SOLD;
        this.timeToLive = (long) -1;
    }

    public void removeExpiration() {
        this.timeToLive = -1L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketReserve ticketReserve)) return false;
        return ticketId != null && ticketId.equals(ticketReserve.ticketId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
