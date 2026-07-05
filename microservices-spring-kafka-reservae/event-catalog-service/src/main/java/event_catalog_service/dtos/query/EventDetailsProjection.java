package event_catalog_service.dtos.query;

import event_catalog_service.entities.enums.EventStatusEnum;

import java.time.LocalDateTime;

public interface EventDetailsProjection {
    String getEventId();

    String getTitle();

    LocalDateTime getEventDate();

    EventStatusEnum getStatus();

    String getVenueName();

    String getVenueCity();

    String getVenueState();
}
