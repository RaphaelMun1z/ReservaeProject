package order_service.proxy.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {
    @PostMapping("/inventory-service/api/inventory/reserve-ticket/{ticketTag}/user/{userId}")
    TicketStatusResponseDTO tryReserveTicket(@PathVariable String ticketTag, @PathVariable String userId);

    @PostMapping("/inventory-service/api/inventory/release/{ticketTag}")
    void releaseTicket(@PathVariable String ticketTag);
}
