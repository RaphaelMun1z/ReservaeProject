package inventory_service.entities;

import inventory_service.entities.enums.TicketStatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash("TicketReserve")
public class TicketReserve {
    @Id
    private String ticketTag;

    @Indexed
    private String eventId;

    @Indexed
    private String sectorId;

    @Indexed
    private String userId;

    @Indexed
    private TicketStatusEnum status;

    @TimeToLive
    private Long ttl;

    private static final long LOCK_TTL_SECONDS = 600L;

    public TicketReserve() {
    }

    public TicketReserve(String eventId, String sectorId, String userId, TicketStatusEnum status) {
        this.ticketTag = UUID.randomUUID()
                .toString();
        this.eventId = eventId;
        this.sectorId = sectorId;
        this.userId = userId;
        this.status = status;
        this.ttl = (long) -1;
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

    public String getTicketTag() {
        return ticketTag;
    }

    public TicketStatusEnum getStatus() {
        return status;
    }

    public Long getTtl() {
        return ttl;
    }

    // Métodos auxiliares
    public void reserve(String userId) {
        this.userId = userId;
        this.status = TicketStatusEnum.RESERVED;
        this.ttl = LOCK_TTL_SECONDS;
    }

    public void release() {
        this.status = TicketStatusEnum.AVAILABLE;
        this.ttl = (long) -1;
    }

    public void sold() {
        this.status = TicketStatusEnum.SOLD;
        this.ttl = (long) -1;
    }

    public void removeExpiration() {
        this.ttl = -1L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketReserve ticketReserve)) return false;
        return ticketTag != null && ticketTag.equals(ticketReserve.ticketTag);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TicketReserve{" + ", ticketTag='" + ticketTag + '\'' + ", eventId='" + eventId + '\'' + ", sectorId='" + sectorId + '\'' + ", userId='" + userId + '\'' + ", status=" + status + ", ttl=" + ttl + '}';
    }
}
