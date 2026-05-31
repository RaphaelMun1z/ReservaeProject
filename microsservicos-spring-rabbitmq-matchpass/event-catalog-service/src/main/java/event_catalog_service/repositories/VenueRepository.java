package event_catalog_service.repositories;

import event_catalog_service.entities.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, String> {
    @Query("""
            SELECT DISTINCT v
            FROM Venue v
            LEFT JOIN FETCH v.sectors
        """)
    List<Venue> findAllWithSectors();

    @Query("""
            SELECT v
            FROM Venue v
            LEFT JOIN FETCH v.sectors
            WHERE v.id = :id
        """)
    Optional<Venue> findByIdWithSectors(String id);

    @Query("""
            SELECT DISTINCT v
            FROM Venue v
            LEFT JOIN FETCH v.sectors
            WHERE LOWER(v.city) = LOWER(:city)
              AND LOWER(v.state) = LOWER(:state)
        """)
    List<Venue> findByLocationWithSectors(
        String city,
        String state
    );

    Optional<Venue> findByNameAndCityAndState(String name, String city, String state);
}
