package inventory_service.repositories;

import inventory_service.entities.SeatLock;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SeatLockRepository extends CrudRepository<SeatLock, String> {
    List<SeatLock> findByEventIdAndSectorId(String eventId, String sectorId);

    Optional<SeatLock> findByEventIdAndSectorIdAndSeatNumber(String eventId, String sectorId, String seatNumber);
}
