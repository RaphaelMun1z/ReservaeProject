package event_catalog_service.repositories;

import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.entities.EventSectorPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventSectorPricingRepository extends JpaRepository<EventSectorPricing, String> {
    @Query("""
                SELECT new event_catalog_service.dtos.res.EventSectorDetailsDTO(
                    esp.event.id, esp.sectorId, s.name, esp.basePrice, esp.halfPrice, s.hasNumberedTickets, s.capacity
                )
                FROM EventSectorPricing esp
                JOIN Sector s ON esp.sectorId = s.id
                WHERE esp.event.id = :eventId
            """)
    List<EventSectorDetailsDTO> findEventSectorsDetailsByEventId(@Param("eventId") String eventId);

    @Query("""
                SELECT new event_catalog_service.dtos.res.EventSectorDetailsDTO(
                    esp.event.id, esp.sectorId, s.name, esp.basePrice, esp.halfPrice, s.hasNumberedTickets, s.capacity
                )
                FROM EventSectorPricing esp
                JOIN Sector s ON esp.sectorId = s.id
                WHERE esp.event.id = :eventId AND esp.sectorId = :sectorId
            """)
    Optional<EventSectorDetailsDTO> findEventSectorDetailsByEventIdAndSectorId(String eventId, String sectorId);

    boolean existsByEvent_IdAndSectorId(
            String eventId,
            String sectorId
    );
}
