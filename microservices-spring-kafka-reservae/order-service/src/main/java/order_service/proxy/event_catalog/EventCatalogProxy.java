package order_service.proxy.event_catalog;

import order_service.proxy.event_catalog.config.EventCatalogFeignConfig;
import order_service.proxy.event_catalog.dtos.EventSectorPriceResponseDTO;
import order_service.proxy.event_catalog.dtos.SectorsTicketPriceRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    @PostMapping("/event-catalog-service/api/events/v1/tickets/price")
    List<EventSectorPriceResponseDTO> consultTicketsPrice(
        SectorsTicketPriceRequestDTO sectorsTicketPriceRequestDTO
    );
}