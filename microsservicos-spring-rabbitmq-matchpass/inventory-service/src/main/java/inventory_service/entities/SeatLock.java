package inventory_service.entities;

import inventory_service.entities.enums.SeatStatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("SeatLock")
public class SeatLock {
    @Id
    private String lockId;

    @Indexed
    private String eventId;

    @Indexed
    private String sectorId;

    private String userId;
    private Integer quantity;
    private String seatNumber;
    private SeatStatusEnum status;

    @TimeToLive
    private Long ttl;

    public SeatLock() {
    }

    public SeatLock(String lockId, String eventId, String sectorId, String userId, Integer quantity, String seatNumber, SeatStatusEnum status, Long ttl) {
        this.lockId = lockId;
        this.eventId = eventId;
        this.sectorId = sectorId;
        this.userId = userId;
        this.quantity = quantity;
        this.seatNumber = seatNumber;
        this.status = status;
        this.ttl = ttl;
    }

    public String getLockId() {
        return lockId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatStatusEnum getStatus() {
        return status;
    }

    public Long getTtl() {
        return ttl;
    }

    // Métodos auxiliares
    public void lock() {
        this.status = SeatStatusEnum.LOCKED;
    }

    public void sold() {
        this.status = SeatStatusEnum.SOLD;
    }

    public void removeExpiration() {
        this.ttl = -1L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatLock seatLock)) return false;
        return lockId != null && lockId.equals(seatLock.lockId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
