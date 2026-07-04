package ticket_service.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket_service.dtos.req.GenerateTicketRequestDTO;
import ticket_service.entities.Ticket;
import ticket_service.entities.enums.TicketStatusEnum;
import ticket_service.exceptions.models.NotFoundException;
import ticket_service.repositories.TicketRepository;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public List<Ticket> generateFromOrder(GenerateTicketRequestDTO dto) {
        List<Ticket> ticketsToGenerate = dto.ticketsId()
            .stream()
            .map(ticketId -> {
                Ticket ticket = new Ticket(
                    dto.orderId(),
                    dto.eventId(),
                    dto.userId(),
                    dto.sectorId(),
                    ticketId,
                    null,
                    TicketStatusEnum.VALID
                );

                String rawData = dto.orderId() + ticketId + System.currentTimeMillis();
                String qrHash = java.util.UUID.nameUUIDFromBytes(rawData.getBytes())
                    .toString();
                ticket.setQrCodeHash(qrHash);

                return ticket;
            })
            .toList();

        return ticketRepository.saveAll(ticketsToGenerate);
    }

    public Ticket findById(String id) {
        return ticketRepository.findById(id)
            .orElseThrow(
                () -> new NotFoundException("Ticket não encontrado.")
            );
    }

    public List<Ticket> findByUserId(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    public Page<Ticket> findByEventId(String eventId, Pageable pageable) {
        return ticketRepository.findByEventId(
            eventId,
            pageable
        );
    }

    @Transactional
    public void revokeTicket(String id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(
                () -> new NotFoundException("Ticket não encontrado.")
            );
        ticket.revokeTicket();
    }
}
