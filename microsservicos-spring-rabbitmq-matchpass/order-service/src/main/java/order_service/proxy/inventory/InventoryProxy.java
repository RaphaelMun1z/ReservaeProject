package order_service.proxy.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service")
public interface InventoryProxy {
    @PostMapping("/inventory-service/api/inventory/lock-seat/{seatTag}/user/{userId}")
    SeatStatusResponseDTO tryLockSeat(@PathVariable String seatTag, @PathVariable String userId);

    @PostMapping("/inventory-service/api/inventory/release/{seatTag}")
    void releaseSeat(@PathVariable String seatTag);
}
