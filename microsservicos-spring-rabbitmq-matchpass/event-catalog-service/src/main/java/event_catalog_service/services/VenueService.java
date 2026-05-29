package event_catalog_service.services;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.entities.Sector;
import event_catalog_service.entities.Venue;
import event_catalog_service.repositories.SectorRepository;
import event_catalog_service.repositories.VenueRepository;
import event_catalog_service.services.dataHandler.VenueDataHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService extends VenueDataHandler {
    public VenueService(VenueRepository venueRepository, SectorRepository sectorRepository) {
        super(venueRepository, sectorRepository);
    }

    public List<VenueResponseDTO> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        return venueListToDTO(venues);
    }

    public VenueResponseDTO getVenueById(String id) {
        Venue venueFound = findVenueById(id);
        return venueObjToVenueResDTO(venueFound, sectorListToDTO(venueFound.getSectors()));
    }

    public List<VenueResponseDTO> getVenuesByLocation(String city, String state) {
        List<Venue> venuesFound = venueRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state);
        return venueListToDTO(venuesFound);
    }

    @Transactional
    public VenueResponseDTO createVenue(CreateVenueRequestDTO dto) {
        validateTotalCapacity(dto);
        Venue savedVenue = createVenueInstance(dto);
        List<SectorResponseDTO> sectorsDTO = sectorListToDTO(savedVenue.getSectors());
        return venueObjToVenueResDTO(savedVenue, sectorsDTO);
    }

    @Transactional
    public SectorResponseDTO addSectorToVenue(SectorRequestDTO sectorDTO, String venueId) {
        Venue venueFound = findVenueById(venueId);
        Sector savedSector = createSectorInstance(sectorDTO, venueFound);
        venueFound.addSector(savedSector);
        return sectorObjToSectorResDTO(savedSector);
    }

    @Transactional
    public void removeSectorFromVenue(String venueId, String sectorId) {
        Venue venueFound = findVenueById(venueId);
        Sector sectorToRemove = venueFound.getSectors().stream().filter(s -> s.getId().equals(sectorId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Setor com ID " + sectorId + " não encontrado no venue " + venueId));

        venueFound.removeSector(sectorToRemove);
    }
}