package inventory_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-catalog-service")
public interface EventCatalogProxy {
    @GetMapping("/event-catalog-service/api/event/validate/v1/{eventId}/exists")
    String validateEvent(@PathVariable String eventId);

    @GetMapping("/event-catalog-service/api/event/validate/v1/{eventId}/sector/{sectorId}/exists")
    String validateEventSector(
            @PathVariable String eventId,
            @PathVariable String sectorId
    );

    @GetMapping("/event-catalog-service/api/event/validate/v1/{eventId}/sector/{sectorId}/tickets/{ticketsAmount}")
    String validateEventSectorTicketCreating(
            @PathVariable String eventId,
            @PathVariable String sectorId,
            @PathVariable int ticketsAmount
    );
}
