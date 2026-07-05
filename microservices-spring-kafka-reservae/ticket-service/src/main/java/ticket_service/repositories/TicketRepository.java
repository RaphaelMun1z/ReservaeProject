package ticket_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ticket_service.entities.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByUserId(String userId);

    Page<Ticket> findByEventId(String eventId, Pageable pageable);

    Optional<Ticket> findByQrCodeHash(String qrCodeHash);
}
