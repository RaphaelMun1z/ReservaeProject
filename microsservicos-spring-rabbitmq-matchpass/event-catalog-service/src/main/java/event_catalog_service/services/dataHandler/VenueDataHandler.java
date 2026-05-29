package event_catalog_service.services.dataHandler;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.entities.Sector;
import event_catalog_service.entities.Venue;
import event_catalog_service.repositories.SectorRepository;
import event_catalog_service.repositories.VenueRepository;

import java.util.List;

public class VenueDataHandler {
    protected final VenueRepository venueRepository;
    protected final SectorRepository sectorRepository;

    public VenueDataHandler(VenueRepository venueRepository, SectorRepository sectorRepository) {
        this.venueRepository = venueRepository;
        this.sectorRepository = sectorRepository;
    }

    protected Venue createVenueInstance(CreateVenueRequestDTO dto) {
        Venue newVenue = new Venue(dto.name(), dto.city(), dto.state(), dto.totalCapacity());
        List<Sector> sectorsToAdd = sectorDtoListToEntityList(newVenue, dto.sectors());
        newVenue.addMultipleSectors(sectorsToAdd);
        return venueRepository.save(newVenue);
    }

    protected List<Sector> sectorDtoListToEntityList(Venue venue, List<SectorRequestDTO> sectorsDTO) {
        return sectorsDTO.stream().map(dto -> new Sector(venue, dto.name(), dto.capacity(), dto.hasNumberedSeats())).toList();
    }

    protected Sector createSectorInstance(SectorRequestDTO s, Venue savedVenue) {
        Sector newSector = new Sector(savedVenue, s.name(), s.capacity(), s.hasNumberedSeats());
        return sectorRepository.save(newSector);
    }

    protected Venue findVenueById(String id) {
        return venueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Setor não encontrado com o ID: " + id));
    }

    protected void validateTotalCapacity(CreateVenueRequestDTO dto) {
        int totalCapacityExpected = dto.totalCapacity();
        int totalCapacityCount = dto.sectors().stream().mapToInt(SectorRequestDTO::capacity).sum();
        if (totalCapacityCount != totalCapacityExpected) {
            throw new IllegalArgumentException("A capacidade total informada e a soma das capacidades dos setores não são iguais.");
        }
    }

    protected VenueResponseDTO venueObjToVenueResDTO(Venue venue, List<SectorResponseDTO> sectorsResDTO) {
        return new VenueResponseDTO(venue.getId(), venue.getName(), venue.getCity(), venue.getState(), venue.getTotalCapacity(), sectorsResDTO);
    }

    protected SectorResponseDTO sectorObjToSectorResDTO(Sector s) {
        return new SectorResponseDTO(s.getId(), s.getName(), s.getHasNumberedSeats());
    }

    protected List<SectorResponseDTO> sectorListToDTO(List<Sector> sectors) {
        return sectors.stream().map(this::sectorObjToSectorResDTO).toList();
    }

    protected List<VenueResponseDTO> venueListToDTO(List<Venue> venues) {
        return venues.stream().map(venue -> venueObjToVenueResDTO(venue, sectorListToDTO(venue.getSectors()))).toList();
    }
}
