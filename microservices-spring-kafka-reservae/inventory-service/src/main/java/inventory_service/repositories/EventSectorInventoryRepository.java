package inventory_service.repositories;

import inventory_service.entities.EventSectorInventory;
import org.springframework.data.repository.CrudRepository;

public interface EventSectorInventoryRepository extends CrudRepository<EventSectorInventory, String> {
}