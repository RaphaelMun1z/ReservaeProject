package event_catalog_service.repositories;

import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.entities.EventSectorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventSectorPricingRepository extends JpaRepository<EventSectorPricing, String> {
    @Query("""
            SELECT new event_catalog_service.dtos.res.EventSectorDetailsDTO(
                esp.sectorId, s.name, esp.basePrice, esp.halfPrice, s.hasNumberedSeats
            )
            FROM EventSectorPricing esp
            JOIN Sector s ON esp.sectorId = s.id
            WHERE esp.event.id = :eventId
        """)
    List<EventSectorDetailsDTO> findEventSectorsDetailsByEventId(@Param("eventId") String eventId);
}
