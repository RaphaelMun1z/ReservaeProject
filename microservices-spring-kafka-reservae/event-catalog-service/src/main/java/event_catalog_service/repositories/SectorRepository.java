package event_catalog_service.repositories;

import event_catalog_service.entities.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, String> {

}
