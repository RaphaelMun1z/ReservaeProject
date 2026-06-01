package inventory_service.repositories;

import inventory_service.entities.SeatLock;
import inventory_service.entities.enums.SeatStatusEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SeatLockRepository extends CrudRepository<SeatLock, String> {
    List<SeatLock> findByEventIdAndSectorIdAndStatus(String eventId, String sectorId, SeatStatusEnum status);

    List<SeatLock> findByEventIdAndStatus(String eventId, SeatStatusEnum status);

    List<SeatLock> findByUserId(String userId);
}
