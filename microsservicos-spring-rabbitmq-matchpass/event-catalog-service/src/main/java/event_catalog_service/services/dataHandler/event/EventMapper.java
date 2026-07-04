package event_catalog_service.services.dataHandler.event;

import event_catalog_service.dtos.query.EventDetailsProjection;
import event_catalog_service.dtos.res.EventDetailsResponseDTO;
import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.dtos.res.SectorPricingResponseDTO;
import event_catalog_service.entities.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {
    public SectorPricingResponseDTO toSectorPricingResponseDTO(Sector sector, EventSectorPricing esp) {
        return new SectorPricingResponseDTO(
                sector.getId(),
                sector.getName(),
                esp.getBasePrice(),
                esp.getHalfPrice(),
                sector.getHasNumberedTickets()
        );
    }

    public EventDetailsResponseDTO toEventDetailsResponseDTO(Event event, Venue venue, Team homeTeam, Team awayTeam, List<EventSectorDetailsDTO> sectorDetails) {
        return new EventDetailsResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getStatus(),
                venue.getName(),
                venue.getCity(),
                venue.getState(),
                homeTeam.getName(),
                awayTeam.getName(),
                sectorDetails
        );
    }

    public EventDetailsResponseDTO toEventDetailsResponseDTO(EventDetailsProjection event, List<EventSectorDetailsDTO> sectorDetails) {
        return new EventDetailsResponseDTO(
                event.getEventId(),
                event.getTitle(),
                event.getEventDate(),
                event.getStatus(),
                event.getVenueName(),
                event.getVenueCity(),
                event.getVenueState(),
                event.getHomeTeamName(),
                event.getAwayTeamName(),
                sectorDetails
        );
    }
}
