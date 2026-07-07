package order_service.proxy.event_catalog;

import order_service.proxy.event_catalog.config.EventCatalogFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "event-catalog-service",
    configuration = EventCatalogFeignConfig.class
)
public interface EventCatalogProxy {
    @GetMapping("/event-catalog-service/api/events/validate/v1/{eventId}/sector/{sectorId}/exists")
    String validateEventSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    );
}