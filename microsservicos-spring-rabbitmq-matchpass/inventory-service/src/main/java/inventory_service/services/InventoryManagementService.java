package inventory_service.services;

import feign.FeignException;
import inventory_service.dtos.req.SeatReservationRequestDTO;
import inventory_service.dtos.res.SeatResponseDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.SeatLock;
import inventory_service.entities.enums.SeatStatusEnum;
import inventory_service.environment.InstanceInformationService;
import inventory_service.exceptions.models.BusinessException;
import inventory_service.exceptions.models.NotFoundException;
import inventory_service.proxy.EventCatalogProxy;
import inventory_service.repositories.SeatLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryManagementService {
    private Logger logger = LoggerFactory.getLogger(InventoryManagementService.class);

    private final InstanceInformationService informationService;

    private final SeatLockRepository seatLockRepository;
    private final EventCatalogProxy eventCatalogProxy;

    public InventoryManagementService(InstanceInformationService informationService, SeatLockRepository seatLockRepository, EventCatalogProxy eventCatalogProxy) {
        this.informationService = informationService;
        this.seatLockRepository = seatLockRepository;
        this.eventCatalogProxy = eventCatalogProxy;
    }

    @Transactional
    public List<SeatStatusResponseDTO> createSeats(int seatsAmount, SeatReservationRequestDTO seatData) {
        String eventCatalogServicePort;
        try {
            eventCatalogServicePort = eventCatalogProxy.validateEventSectorSeatCreating(
                    seatData.eventId(),
                    seatData.sectorId(),
                    seatsAmount
            );
            System.out.println("eventCatalogServicePort: " + eventCatalogServicePort);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("A criação dos assentos não foi permitida." + ex.getMessage());
        }

        List<SeatLock> newSeats = new ArrayList<>();
        for (int ii = 0; ii < seatsAmount; ii++) {
            newSeats.add(new SeatLock(
                    seatData.eventId(),
                    seatData.sectorId(),
                    null,
                    SeatStatusEnum.AVAILABLE
            ));
        }

        List<SeatLock> savedSeats = new ArrayList<>();
        seatLockRepository.saveAll(newSeats).forEach(savedSeats::add);

        String environment =
                "PORT [INVENTORY]: " + informationService.retrieveServerPort() +
                        "\n" + eventCatalogServicePort;

        return savedSeats.stream()
                .map(sl -> new SeatStatusResponseDTO(
                        sl.getSeatTag(),
                        sl.getStatus(),
                        null,
                        environment
                )).toList();
    }

    public SeatStatusResponseDTO tryLockSeat(String seatTag, String userId) {
        SeatLock seat = seatLockRepository.findBySeatTag(seatTag).orElseThrow(
                () -> new NotFoundException("Assento inexistente.")
        );

        if (seat.getStatus() != SeatStatusEnum.AVAILABLE) {
            throw new BusinessException("Assento indisponível");
        }

        String eventCatalogServicePort;
        try {
            logger.info("Entrou no try");
            eventCatalogServicePort = eventCatalogProxy.validateEventSector(seat.getEventId(), seat.getSectorId());
        } catch (FeignException.NotFound ex) {
            logger.error(ex.getMessage());
            throw new NotFoundException("A reserva dos assentos não foi permitida.");
        } catch (Exception ex) {
            logger.error("Outro erro: {}", ex.getMessage());
            throw new BusinessException("Ocorreu um erro interno.");
        }
        logger.info("Depois do try");

        seat.lock(userId);
        SeatLock updatedSeat = seatLockRepository.save(seat);

        String environment =
                "PORT [INVENTORY]: " + informationService.retrieveServerPort() +
                        "\n" + eventCatalogServicePort;

        return new SeatStatusResponseDTO(
                updatedSeat.getSeatTag(),
                updatedSeat.getStatus(),
                LocalDateTime.now().plusSeconds(updatedSeat.getTtl()),
                environment
        );
    }

    @Transactional
    public List<String> reserveSeats(String userId, List<String> seatTags) {
        if (seatTags == null || seatTags.isEmpty()) {
            throw new BusinessException("O pedido não possui assentos para reservar.");
        }

        List<String> lockedSeatTags = new ArrayList<>();

        try {
            seatTags.forEach(seatTag -> {
                tryLockSeat(seatTag, userId);
                lockedSeatTags.add(seatTag);
            });

            return List.copyOf(lockedSeatTags);
        } catch (RuntimeException ex) {
            logger.error(
                    "Falha ao reservar os assentos. Liberando {} assento(s) já bloqueado(s). Motivo: {}",
                    lockedSeatTags.size(),
                    ex.getMessage()
            );

            lockedSeatTags.forEach(lockedSeatTag -> {
                try {
                    releaseSeat(lockedSeatTag);
                } catch (RuntimeException releaseEx) {
                    logger.error(
                            "Falha ao liberar o assento {} durante a compensação. Motivo: {}",
                            lockedSeatTag,
                            releaseEx.getMessage()
                    );
                }
            });

            throw ex;
        }
    }

    @Transactional
    public void confirmSeatSold(String seatTag) {
        SeatLock seatLock = seatLockRepository.findById(seatTag)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!seatLock.getStatus().equals(SeatStatusEnum.LOCKED)) {
            throw new BusinessException("Assento não reservado previamente.");
        }

        seatLock.sold();
        seatLock.removeExpiration();
        seatLockRepository.save(seatLock);
    }

    @Transactional
    public void releaseSeat(String seatTag) {
        SeatLock seatLock = seatLockRepository.findById(seatTag)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        seatLock.release();
        seatLockRepository.save(seatLock);
    }

    public SeatStatusResponseDTO checkSeatStatus(String seatTag) {
        SeatLock seatLock = seatLockRepository.findById(seatTag)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new SeatStatusResponseDTO(
                seatLock.getSeatTag(),
                seatLock.getStatus(),
                LocalDateTime.now().minusSeconds(seatLock.getTtl()),
                environment
        );
    }

    public List<SeatStatusResponseDTO> findUserSeats(String userId) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return seatLockRepository.findByUserId(userId).stream()
                .map(sl -> new SeatStatusResponseDTO(
                        sl.getSeatTag(),
                        sl.getStatus(),
                        sl.getStatus().equals(SeatStatusEnum.LOCKED) ? LocalDateTime.now().plusSeconds(sl.getTtl()) : null,
                        environment
                )).toList();
    }

    public List<SeatResponseDTO> findEventSeatsByStatus(String eventId, SeatStatusEnum status) {
        if (eventId == null) {
            throw new IllegalArgumentException("O ID do evento não pode ser nulo.");
        }

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return seatLockRepository.findByEventIdAndStatus(eventId, status).stream().map(
                e -> new SeatResponseDTO(
                        e.getSeatTag(),
                        e.getEventId(),
                        e.getSectorId(),
                        environment
                )
        ).toList();
    }

    public SeatResponseDTO findSeatById(String seatTag) {
        SeatLock seat = seatLockRepository.findById(seatTag).orElseThrow(
                () -> new NotFoundException("Assento inexistente.")
        );

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new SeatResponseDTO(
                seat.getSeatTag(),
                seat.getEventId(),
                seat.getSectorId(),
                environment
        );
    }

    public List<SeatResponseDTO> findEventSectorSeatsByStatus(String eventId, String sectorId, SeatStatusEnum status) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return seatLockRepository.findByEventIdAndSectorIdAndStatus(eventId, sectorId, status).stream()
                .map(seat -> new SeatResponseDTO(
                        seat.getSeatTag(),
                        seat.getEventId(),
                        seat.getSectorId(),
                        environment
                )).toList();
    }
}