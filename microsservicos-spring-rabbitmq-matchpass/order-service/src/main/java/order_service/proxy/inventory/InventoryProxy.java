package order_service.proxy.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {
    @PostMapping("/inventory-service/api/inventory/reserve-ticket/{ticketId}/user/{userId}")
    TicketStatusResponseDTO tryReserveTicket(@PathVariable String ticketId, @PathVariable String userId);

    @PostMapping("/inventory-service/api/inventory/release/{ticketId}")
    void releaseTicket(@PathVariable String ticketId);
}
