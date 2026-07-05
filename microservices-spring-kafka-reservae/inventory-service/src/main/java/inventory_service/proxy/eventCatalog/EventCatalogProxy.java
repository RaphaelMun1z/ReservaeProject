package inventory_service.proxy.eventCatalog;

import inventory_service.proxy.eventCatalog.config.EventCatalogFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "event-catalog-service",
    configuration = EventCatalogFeignConfig.class
)
public interface EventCatalogProxy {
    @GetMapping("/event-catalog-service/api/events/validate/v1/{eventId}/exists")
    String validateEvent(@PathVariable String eventId);

    @GetMapping("/event-catalog-service/api/events/validate/v1/{eventId}/sector/{sectorId}/exists")
    String validateEventSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    );

    @GetMapping("/event-catalog-service/api/events/validate/v1/{eventId}/sector/{sectorId}/validate-capacity/{ticketsAmount}")
    String validateSectorCapacity(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int ticketsAmount
    );
}
