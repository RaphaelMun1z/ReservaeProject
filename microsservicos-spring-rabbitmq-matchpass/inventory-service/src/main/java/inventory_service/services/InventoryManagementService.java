package inventory_service.services;

import feign.FeignException;
import inventory_service.dtos.req.TicketReservationRequestDTO;
import inventory_service.dtos.res.TicketResponseDTO;
import inventory_service.dtos.res.TicketStatusResponseDTO;
import inventory_service.entities.TicketReserve;
import inventory_service.entities.enums.TicketStatusEnum;
import inventory_service.environment.InstanceInformationService;
import inventory_service.exceptions.models.BusinessException;
import inventory_service.exceptions.models.NotFoundException;
import inventory_service.proxy.EventCatalogProxy;
import inventory_service.repositories.TicketReserveRepository;
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

    private final TicketReserveRepository ticketRepository;
    private final EventCatalogProxy eventCatalogProxy;

    public InventoryManagementService(InstanceInformationService informationService, TicketReserveRepository ticketRepository, EventCatalogProxy eventCatalogProxy) {
        this.informationService = informationService;
        this.ticketRepository = ticketRepository;
        this.eventCatalogProxy = eventCatalogProxy;
    }

    @Transactional
    public List<TicketStatusResponseDTO> createTickets(int ticketsAmount, TicketReservationRequestDTO ticketData) {
        String eventCatalogServicePort;
        try {
            eventCatalogServicePort = eventCatalogProxy.validateEventSectorTicketCreating(
                    ticketData.eventId(),
                    ticketData.sectorId(),
                    ticketsAmount
            );
            System.out.println("eventCatalogServicePort: " + eventCatalogServicePort);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("A criação dos assentos não foi permitida." + ex.getMessage());
        }

        List<TicketReserve> newTickets = new ArrayList<>();
        for (int ii = 0; ii < ticketsAmount; ii++) {
            newTickets.add(new TicketReserve(
                    ticketData.eventId(),
                    ticketData.sectorId(),
                    null,
                    TicketStatusEnum.AVAILABLE
            ));
        }

        List<TicketReserve> savedTickets = new ArrayList<>();
        ticketRepository.saveAll(newTickets)
                .forEach(savedTickets::add);

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort() + "\n" + eventCatalogServicePort;

        return savedTickets.stream()
                .map(sl -> new TicketStatusResponseDTO(
                        sl.getTicketTag(),
                        sl.getStatus(),
                        null,
                        environment
                ))
                .toList();
    }

    public TicketStatusResponseDTO tryReserveTicket(String ticketTag, String userId) {
        TicketReserve ticket = ticketRepository.findByTicketTag(ticketTag)
                .orElseThrow(() -> new NotFoundException("Assento inexistente."));

        if (ticket.getStatus() != TicketStatusEnum.AVAILABLE) {
            throw new BusinessException("Assento indisponível");
        }

        String eventCatalogServicePort;
        try {
            eventCatalogServicePort = eventCatalogProxy.validateEventSector(
                    ticket.getEventId(),
                    ticket.getSectorId()
            );
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("A reserva dos assentos não foi permitida.");
        } catch (Exception ex) {
            throw new BusinessException("Ocorreu um erro interno.");
        }

        ticket.reserve(userId);
        TicketReserve updatedTicket = ticketRepository.save(ticket);

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort() + "\n" + eventCatalogServicePort;

        return new TicketStatusResponseDTO(
                updatedTicket.getTicketTag(),
                updatedTicket.getStatus(),
                LocalDateTime.now()
                        .plusSeconds(updatedTicket.getTtl()),
                environment
        );
    }

    @Transactional
    public List<String> reserveTickets(String userId, List<String> ticketTags) {
        if (ticketTags == null || ticketTags.isEmpty()) {
            throw new BusinessException("O pedido não possui assentos para reservar.");
        }

        List<String> reservedTicketTags = new ArrayList<>();

        try {
            ticketTags.forEach(ticketTag -> {
                tryReserveTicket(
                        ticketTag,
                        userId
                );
                reservedTicketTags.add(ticketTag);
            });

            return List.copyOf(reservedTicketTags);
        } catch (RuntimeException ex) {
            logger.error(
                    "Falha ao reservar os assentos. Liberando {} assento(s) já bloqueado(s). Motivo: {}",
                    reservedTicketTags.size(),
                    ex.getMessage()
            );

            reservedTicketTags.forEach(reservedTicketTag -> {
                try {
                    releaseTicket(reservedTicketTag);
                } catch (RuntimeException releaseEx) {
                    logger.error(
                            "Falha ao liberar o assento {} durante a compensação. Motivo: {}",
                            reservedTicketTag,
                            releaseEx.getMessage()
                    );
                }
            });

            throw ex;
        }
    }

    @Transactional
    public void confirmTicketSold(String ticketTag) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketTag)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!ticketReserve.getStatus()
                .equals(TicketStatusEnum.RESERVED)) {
            throw new BusinessException("Assento não reservado previamente.");
        }

        ticketReserve.sold();
        ticketReserve.removeExpiration();
        ticketRepository.save(ticketReserve);
    }

    @Transactional
    public void releaseTicket(String ticketTag) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketTag)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        ticketReserve.release();
        ticketRepository.save(ticketReserve);
    }

    public TicketStatusResponseDTO checkTicketStatus(String ticketTag) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketTag)
                .orElseThrow(() -> new NotFoundException("Ingresso não encontrado"));

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new TicketStatusResponseDTO(
                ticketReserve.getTicketTag(),
                ticketReserve.getStatus(),
                LocalDateTime.now()
                        .minusSeconds(ticketReserve.getTtl()),
                environment
        );
    }

    public List<TicketStatusResponseDTO> findUserTickets(String userId) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return ticketRepository.findByUserId(userId)
                .stream()
                .map(sl -> new TicketStatusResponseDTO(
                        sl.getTicketTag(),
                        sl.getStatus(),
                        sl.getStatus()
                                .equals(TicketStatusEnum.RESERVED) ? LocalDateTime.now()
                                .plusSeconds(sl.getTtl()) : null,
                        environment
                ))
                .toList();
    }

    public List<TicketResponseDTO> findEventTicketsByStatus(String eventId, TicketStatusEnum status) {
        if (eventId == null) {
            throw new IllegalArgumentException("O ID do evento não pode ser nulo.");
        }

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return ticketRepository.findByEventIdAndStatus(
                        eventId,
                        status
                )
                .stream()
                .map(e -> new TicketResponseDTO(
                        e.getTicketTag(),
                        e.getEventId(),
                        e.getSectorId(),
                        environment
                ))
                .toList();
    }

    public TicketResponseDTO findTicketById(String ticketTag) {
        TicketReserve ticket = ticketRepository.findById(ticketTag)
                .orElseThrow(() -> new NotFoundException("Assento inexistente."));

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new TicketResponseDTO(
                ticket.getTicketTag(),
                ticket.getEventId(),
                ticket.getSectorId(),
                environment
        );
    }

    public List<TicketResponseDTO> findEventSectorTicketsByStatus(String eventId, String sectorId, TicketStatusEnum status) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return ticketRepository.findByEventIdAndSectorIdAndStatus(
                        eventId,
                        sectorId,
                        status
                )
                .stream()
                .map(ticket -> new TicketResponseDTO(
                        ticket.getTicketTag(),
                        ticket.getEventId(),
                        ticket.getSectorId(),
                        environment
                ))
                .toList();
    }
}