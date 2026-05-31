package inventory_service.services;

import feign.FeignException;
import inventory_service.clients.CatalogServiceClient;
import inventory_service.dtos.req.SeatReservationRequestDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.SeatLock;
import inventory_service.entities.enums.SeatStatusEnum;
import inventory_service.environment.InstanceInformationService;
import inventory_service.exceptions.models.BusinessException;
import inventory_service.exceptions.models.NotFoundException;
import inventory_service.repositories.SeatLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryManagementService {
    private final InstanceInformationService informationService;

    private final SeatLockRepository seatLockRepository;
    private final CatalogServiceClient catalogServiceClient;

    public InventoryManagementService(InstanceInformationService informationService, SeatLockRepository seatLockRepository, CatalogServiceClient catalogServiceClient) {
        this.informationService = informationService;
        this.seatLockRepository = seatLockRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    @Transactional
    public List<SeatStatusResponseDTO> createSeats(int seatsAmount, SeatReservationRequestDTO seatData) {
        try {
            catalogServiceClient.validateEventSectorSeatCreating(
                seatData.eventId(),
                seatData.sectorId(),
                seatsAmount
            );
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("A criação dos assentos não foi permitida.");
        }

        List<SeatLock> newSeats = new ArrayList<>();
        for (int ii = 0; ii < seatsAmount; ii++) {
            String tag = "seat_tag_" + UUID.randomUUID();

            newSeats.add(new SeatLock(
                seatData.eventId(),
                seatData.sectorId(),
                null,
                SeatStatusEnum.AVAILABLE
            ));
        }

        List<SeatLock> savedSeats = new ArrayList<>();
        seatLockRepository.saveAll(newSeats).forEach(savedSeats::add);
        return savedSeats.stream()
            .map(sl -> new SeatStatusResponseDTO(
                sl.getSeatTag(),
                sl.getStatus(),
                null,
                "PORT: " + informationService.retrieveServerPort()
            )).toList();
    }

    public SeatStatusResponseDTO tryLockSeat(SeatReservationRequestDTO dto, String userId, String port) {
        try {
            catalogServiceClient.validateEventSector(dto.eventId(), dto.sectorId());
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("A reserva dos assentos não foi permitida." + ex.getMessage());
        }

        Optional<SeatLock> existingLock = seatLockRepository.findById(dto.seatTag());

        SeatLock seat = existingLock.orElseThrow(
            () -> new NotFoundException("Assento inexistente.")
        );

        if (seat.getStatus() != SeatStatusEnum.AVAILABLE) {
            throw new BusinessException("Assento indisponível");
        }

        seat.lock(userId);

        SeatLock updatedSeat = seatLockRepository.save(seat);

        return new SeatStatusResponseDTO(
            updatedSeat.getSeatTag(),
            updatedSeat.getStatus(),
            LocalDateTime.now().plusSeconds(updatedSeat.getTtl()),
            port
        );
    }

    @Transactional
    public void confirmSeatSold(String seatTag) {
        SeatLock seatLock = seatLockRepository.findById(seatTag).orElseThrow(() -> new RuntimeException("Lock não encontrado"));
        if (!seatLock.getStatus().equals(SeatStatusEnum.LOCKED)) {
            throw new BusinessException("Assento não reservado previamente.");
        }
        seatLock.sold();
        seatLock.removeExpiration();
        seatLockRepository.save(seatLock);
    }

    @Transactional
    public void releaseSeat(String seatTag) {
        SeatLock seatLock = seatLockRepository.findById(seatTag).orElseThrow(() -> new RuntimeException("Lock não encontrado"));
        seatLock.release();
        seatLockRepository.save(seatLock);
    }

    public SeatStatusResponseDTO checkSeatStatus(String seatTag, String port) {
        SeatLock seatLock = seatLockRepository.findById(seatTag)
            .orElseThrow(() -> new RuntimeException("Assento não encontrado ou expirado"));

        return new SeatStatusResponseDTO(
            seatLock.getSeatTag(),
            seatLock.getStatus(),
            LocalDateTime.now().minusSeconds(seatLock.getTtl()),
            port
        );
    }

    public List<SeatStatusResponseDTO> findUserSeats(String userId, String port) {
        return seatLockRepository.findByUserId(userId).stream()
            .map(sl -> new SeatStatusResponseDTO(
                sl.getSeatTag(),
                sl.getStatus(),
                sl.getStatus().equals(SeatStatusEnum.LOCKED) ? LocalDateTime.now().plusSeconds(sl.getTtl()) : null,
                port
            )).toList();
    }

    public List<SeatStatusResponseDTO> findEventAvailableSeats(String eventId, String port) {
        return seatLockRepository.findByEventIdAndStatus()
    }
}