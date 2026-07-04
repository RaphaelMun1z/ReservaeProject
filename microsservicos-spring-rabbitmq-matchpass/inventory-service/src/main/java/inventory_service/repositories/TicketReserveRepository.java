package inventory_service.repositories;

import inventory_service.entities.TicketReserve;
import inventory_service.entities.enums.TicketStatusEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TicketReserveRepository extends CrudRepository<TicketReserve, String> {
    Optional<TicketReserve> findByTicketTag(String ticketTag);

    List<TicketReserve> findByEventIdAndSectorIdAndStatus(String eventId, String sectorId, TicketStatusEnum status);

    List<TicketReserve> findByEventIdAndStatus(String eventId, TicketStatusEnum status);

    List<TicketReserve> findByUserId(String userId);
}
