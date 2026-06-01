package inventory_service.entities;

import inventory_service.entities.enums.SeatStatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash("SeatLock")
public class SeatLock {
    @Id
    private String seatTag;

    @Indexed
    private String eventId;

    @Indexed
    private String sectorId;

    @Indexed
    private String userId;

    @Indexed
    private SeatStatusEnum status;

    @TimeToLive
    private Long ttl;

    private static final long LOCK_TTL_SECONDS = 600L;

    public SeatLock() {
    }

    public SeatLock(
        String eventId,
        String sectorId,
        String userId,
        SeatStatusEnum status) {
        this.seatTag = UUID.randomUUID().toString();
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

    public String getSeatTag() {
        return seatTag;
    }

    public SeatStatusEnum getStatus() {
        return status;
    }

    public Long getTtl() {
        return ttl;
    }

    // Métodos auxiliares
    public void lock(String userId) {
        this.userId = userId;
        this.status = SeatStatusEnum.LOCKED;
        this.ttl = LOCK_TTL_SECONDS;
    }

    public void release() {
        this.status = SeatStatusEnum.AVAILABLE;
        this.ttl = (long) -1;
    }

    public void sold() {
        this.status = SeatStatusEnum.SOLD;
        this.ttl = (long) -1;
    }

    public void removeExpiration() {
        this.ttl = -1L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatLock seatLock)) return false;
        return seatTag != null && seatTag.equals(seatLock.seatTag);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SeatLock{" +
            ", seatTag='" + seatTag + '\'' +
            ", eventId='" + eventId + '\'' +
            ", sectorId='" + sectorId + '\'' +
            ", userId='" + userId + '\'' +
            ", status=" + status +
            ", ttl=" + ttl +
            '}';
    }
}
