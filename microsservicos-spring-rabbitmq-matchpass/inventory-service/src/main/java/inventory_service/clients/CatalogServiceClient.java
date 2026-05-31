package inventory_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "event-catalog-service",
    url = "${event-catalog.service.url}"
)
public interface CatalogServiceClient {
    @GetMapping("/api/event/validate/v1/{eventId}/exists")
    void validateEvent(@PathVariable String eventId);

    @GetMapping("/api/event/validate/v1/{eventId}/sector/{sectorId}/exists")
    void validateEventSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    );

    @GetMapping("/api/event/validate/v1/{eventId}/sector/{sectorId}/seats/{seatsAmount}")
    void validateEventSectorSeatCreating(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int seatsAmount
    );
}