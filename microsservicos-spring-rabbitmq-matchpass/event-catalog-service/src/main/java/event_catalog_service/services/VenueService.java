package event_catalog_service.services;

import event_catalog_service.dtos.req.CreateVenueRequestDTO;
import event_catalog_service.dtos.req.SectorRequestDTO;
import event_catalog_service.dtos.res.SectorResponseDTO;
import event_catalog_service.dtos.res.VenueResponseDTO;
import event_catalog_service.entities.Sector;
import event_catalog_service.entities.Venue;
import event_catalog_service.exceptions.models.DuplicatedResourceException;
import event_catalog_service.exceptions.models.NotFoundException;
import event_catalog_service.repositories.SectorRepository;
import event_catalog_service.repositories.VenueRepository;
import event_catalog_service.services.dataHandler.venue.VenueMapper;
import event_catalog_service.services.dataHandler.venue.VenueQueryService;
import event_catalog_service.services.dataHandler.venue.VenueValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {
    private final VenueRepository venueRepository;
    private final SectorRepository sectorRepository;

    private final VenueMapper mapper;
    private final VenueValidator validator;
    private final VenueQueryService queryService;

    public VenueService(
        VenueRepository venueRepository,
        SectorRepository sectorRepository,
        VenueQueryService queryService,
        VenueMapper mapper,
        VenueValidator validator
    ) {
        this.venueRepository = venueRepository;
        this.sectorRepository = sectorRepository;
        this.queryService = queryService;
        this.mapper = mapper;
        this.validator = validator;
    }

    public List<VenueResponseDTO> findAllVenuesWithSectors() {
        return mapper.toVenueResponseDTOList(queryService.findAllVenuesWithSectors());
    }

    public VenueResponseDTO findVenueById(String id) {
        return mapper.toVenueResponseDTO(queryService.findVenueById(id));
    }

    public List<VenueResponseDTO> findVenuesByLocation(String city, String state) {
        return mapper.toVenueResponseDTOList(queryService.findVenuesByLocation(city, state));
    }

    @Transactional
    public VenueResponseDTO createVenue(CreateVenueRequestDTO dto) {
        validator.validateTotalCapacity(dto);
        validateDuplicatedVenue(dto);

        Venue venue = new Venue(
            dto.name(),
            dto.city(),
            dto.state(),
            dto.totalCapacity()
        );

        List<Sector> sectors =
            dto.sectors()
                .stream()
                .map(s ->
                    new Sector(
                        venue,
                        s.name(),
                        s.capacity(),
                        s.hasNumberedSeats()
                    )
                )
                .toList();

        venue.addMultipleSectors(sectors);

        Venue savedVenue = venueRepository.save(venue);

        return mapper.toVenueResponseDTO(savedVenue);
    }

    private void validateDuplicatedVenue(CreateVenueRequestDTO dto) {
        System.out.println(dto.name());
        System.out.println(dto.city());
        System.out.println(dto.state());
        venueRepository.findByNameAndCityAndState(
            dto.name(),
            dto.city(),
            dto.state()
        ).ifPresent(venue -> {
            throw new DuplicatedResourceException("O local informado já foi registrado.");
        });
    }

    @Transactional
    public SectorResponseDTO addSectorToVenue(SectorRequestDTO dto, String venueId) {
        Venue venue = queryService.findVenueById(venueId);

        Sector sector = new Sector(
            venue,
            dto.name(),
            dto.capacity(),
            dto.hasNumberedSeats()
        );
        Sector savedSector = sectorRepository.save(sector);

        venue.addSector(savedSector);

        return mapper.toSectorResponseDTO(savedSector);
    }

    @Transactional
    public void removeSectorFromVenue(String venueId, String sectorId) {
        Venue venue = queryService.findVenueById(venueId);

        Sector sector =
            venue.getSectors()
                .stream()
                .filter(s ->
                    s.getId().equals(sectorId)
                )
                .findFirst()
                .orElseThrow(() ->
                    new NotFoundException("Setor não encontrado")
                );

        venue.removeSector(sector);
    }
}