package order_service.repositories;

import order_service.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByEventId(String eventId);

    List<Order> findByUserId(String userId);

    Optional<Order> findByItemsReservationId(String reservationId);
}