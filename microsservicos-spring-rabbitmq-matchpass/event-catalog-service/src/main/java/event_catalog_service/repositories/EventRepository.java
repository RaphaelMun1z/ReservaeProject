package event_catalog_service.repositories;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {
    @Query(value = """
        SELECT
            e.id AS eventId,
            e.title AS title,
            e.event_date AS eventDate,
            e.status AS status,
        
            v.name AS venueName,
            v.city AS venueCity,
            v.state AS venueState,
        
            ht.name AS homeTeamName,
            at.name AS awayTeamName
        
        FROM tb_events e
        
        INNER JOIN tb_venues v
            ON v.id = e.venue_id
        
        INNER JOIN tb_teams ht
            ON ht.id = e.home_team_id
        
        INNER JOIN tb_teams at
            ON at.id = e.away_team_id
        
        WHERE e.id = :eventId
        """, nativeQuery = true)
    Optional<EventDetailsProjection> findEventDetailsByEventId(String eventId);
}
