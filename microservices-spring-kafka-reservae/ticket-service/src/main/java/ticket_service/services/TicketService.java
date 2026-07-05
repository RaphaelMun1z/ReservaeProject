package ticket_service.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket_service.entities.Ticket;
import ticket_service.entities.enums.TicketStatusEnum;
import ticket_service.exceptions.models.NotFoundException;
import ticket_service.messaging.event.OrderConfirmedEvent;
import ticket_service.messaging.event.OrderConfirmedItemEvent;
import ticket_service.messaging.event.TicketGeneratedEvent;
import ticket_service.messaging.event.TicketGeneratedItemEvent;
import ticket_service.messaging.publisher.TicketGeneratedPublisher;
import ticket_service.repositories.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final TicketGeneratedPublisher ticketGeneratedPublisher;

    public TicketService(
        TicketRepository ticketRepository,
        QrCodeService qrCodeService,
        TicketGeneratedPublisher ticketGeneratedPublisher
    ) {
        this.ticketRepository = ticketRepository;
        this.qrCodeService = qrCodeService;
        this.ticketGeneratedPublisher = ticketGeneratedPublisher;
    }

    @Transactional
    public List<Ticket> generateFromConfirmedOrder(OrderConfirmedEvent event) {
        if (event.items() == null || event.items().isEmpty()) {
            throw new IllegalArgumentException("O pedido confirmado não possui itens para gerar ingressos.");
        }

        List<Ticket> ticketsToGenerate = new ArrayList<>();

        for (OrderConfirmedItemEvent item : event.items()) {
            for (int index = 0; index < item.quantity(); index++) {
                String qrCodeHash = qrCodeService.generateQrCodeHash(
                    event.orderId(),
                    item.orderItemId(),
                    item.reservationId(),
                    event.userId(),
                    event.eventId(),
                    item.sectorId()
                );

                Ticket ticket = new Ticket(
                    event.orderId(),
                    event.eventId(),
                    event.userId(),
                    item.sectorId(),
                    item.reservationId(),
                    item.ticketType(),
                    qrCodeHash,
                    TicketStatusEnum.VALID
                );

                ticketsToGenerate.add(ticket);
            }
        }

        List<Ticket> generatedTickets = ticketRepository.saveAll(ticketsToGenerate);

        ticketGeneratedPublisher.publish(
            toTicketGeneratedEvent(
                event,
                generatedTickets
            )
        );

        return generatedTickets;
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

    public Page<Ticket> findByEventId(
        String eventId,
        Pageable pageable
    ) {
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

    private TicketGeneratedEvent toTicketGeneratedEvent(
        OrderConfirmedEvent orderConfirmedEvent,
        List<Ticket> tickets
    ) {
        List<TicketGeneratedItemEvent> generatedItems = tickets.stream()
            .map(ticket -> new TicketGeneratedItemEvent(
                ticket.getId(),
                ticket.getSectorId(),
                ticket.getTicketType(),
                ticket.getQrCodeHash()
            ))
            .toList();

        return new TicketGeneratedEvent(
            UUID.randomUUID().toString(),
            orderConfirmedEvent.orderId(),
            orderConfirmedEvent.eventId(),
            orderConfirmedEvent.userId(),
            generatedItems,
            LocalDateTime.now()
        );
    }
}