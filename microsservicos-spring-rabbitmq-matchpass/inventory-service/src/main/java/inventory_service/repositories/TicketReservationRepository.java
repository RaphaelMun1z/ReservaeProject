package inventory_service.repositories;

import inventory_service.entities.TicketReservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TicketReservationRepository extends CrudRepository<TicketReservation, String> {
    Optional<TicketReservation> findByReservationId(String reservationId);

    List<TicketReservation> findByOrderId(String orderId);

    List<TicketReservation> findByUserId(String userId);
}
