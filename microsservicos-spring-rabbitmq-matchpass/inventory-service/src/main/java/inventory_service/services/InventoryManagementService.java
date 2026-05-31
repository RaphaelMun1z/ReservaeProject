package inventory_service.services;

import inventory_service.dtos.req.SeatReservationRequestDTO;
import inventory_service.dtos.res.SeatStatusResponseDTO;
import inventory_service.entities.SeatLock;
import inventory_service.entities.enums.SeatStatusEnum;
import inventory_service.repositories.SeatLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryManagementService {
    private final SeatLockRepository seatLockRepository;
    private static final long LOCK_TTL_SECONDS = 600L;

    public InventoryManagementService(SeatLockRepository seatLockRepository) {
        this.seatLockRepository = seatLockRepository;
    }

    public String testeDeComunicacao(String seatNumber) {
        return "Verificando a disponibilidade do assento: " + seatNumber;
    }

    @Transactional
    public SeatStatusResponseDTO tryLockSeat(SeatReservationRequestDTO dto, String userId) {
        Optional<SeatLock> existingLock = seatLockRepository.findByEventIdAndSectorIdAndSeatNumber(dto.eventId(), dto.sectorId(), dto.seatNumber());

        if (existingLock.isPresent()) {
            throw new IllegalStateException("Assento já reservado");
        }

        SeatLock seatLock = new SeatLock(
            UUID.randomUUID().toString(),
            dto.eventId(),
            dto.sectorId(),
            userId,
            dto.quantity(),
            dto.seatNumber(),
            SeatStatusEnum.LOCKED,
            LOCK_TTL_SECONDS
        );
        SeatLock savedSeatLock = seatLockRepository.save(seatLock);

        return new SeatStatusResponseDTO(
            savedSeatLock.getLockId(),
            SeatStatusEnum.LOCKED,
            LocalDateTime.now().plusSeconds(LOCK_TTL_SECONDS)
        );
    }

    @Transactional
    public void confirmSeatSold(String lockId) {
        SeatLock seatLock = seatLockRepository.findById(lockId).orElseThrow(() -> new RuntimeException("Lock não encontrado"));
        seatLock.sold();
        seatLock.removeExpiration();
        seatLockRepository.save(seatLock);
    }

    @Transactional
    public void releaseSeat(String lockId) {
        SeatLock seatLock = seatLockRepository.findById(lockId).orElseThrow(() -> new RuntimeException("Lock não encontrado"));
        seatLockRepository.delete(seatLock);
    }
}
