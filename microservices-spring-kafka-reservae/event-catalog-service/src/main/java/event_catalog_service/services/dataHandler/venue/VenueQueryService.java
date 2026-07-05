package event_catalog_service.services.dataHandler.venue;

import event_catalog_service.entities.Venue;
import event_catalog_service.exceptions.models.NotFoundException;
import event_catalog_service.repositories.VenueRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VenueQueryService {
    private final VenueRepository venueRepository;

    public VenueQueryService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue findVenueById(String id) {
        return venueRepository.findByIdWithSectors(id)
            .orElseThrow(() -> new NotFoundException("Venue não encontrado com o ID: " + id));
    }

    public List<Venue> findAllVenuesWithSectors() {
        return venueRepository.findAllWithSectors();
    }

    public List<Venue> findVenuesByLocation(String city, String state) {
        return venueRepository.findByLocationWithSectors(
            city,
            state
        );
    }
}
