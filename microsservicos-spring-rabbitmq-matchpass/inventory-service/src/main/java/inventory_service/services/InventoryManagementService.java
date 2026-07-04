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
import inventory_service.proxy.eventCatalog.EventCatalogProxy;
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
    private final InstanceInformationService informationService;
    private final TicketReserveRepository ticketRepository;
    private final EventCatalogProxy eventCatalogProxy;
    private Logger logger = LoggerFactory.getLogger(InventoryManagementService.class);

    public InventoryManagementService(
        InstanceInformationService informationService,
        TicketReserveRepository ticketRepository,
        EventCatalogProxy eventCatalogProxy
    ) {
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
                sl.getTicketId(),
                sl.getTicketStatus(),
                null,
                environment
            ))
            .toList();
    }

    public TicketStatusResponseDTO tryReserveTicket(String ticketId, String userId) {
        TicketReserve ticket = ticketRepository.findByTicketId(ticketId)
            .orElseThrow(() -> new NotFoundException("Assento inexistente."));

        if (ticket.getTicketStatus() != TicketStatusEnum.AVAILABLE) {
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
            updatedTicket.getTicketId(),
            updatedTicket.getTicketStatus(),
            LocalDateTime.now()
                .plusSeconds(updatedTicket.getTimeToLive()),
            environment
        );
    }

    @Transactional
    public List<String> reserveTickets(String userId, List<String> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            throw new BusinessException("O pedido não possui assentos para reservar.");
        }

        List<String> reservedTicketIds = new ArrayList<>();
        try {
            ticketIds.forEach(ticketId -> {
                tryReserveTicket(ticketId, userId);
                reservedTicketIds.add(ticketId);
            });

            return List.copyOf(reservedTicketIds);
        } catch (RuntimeException ex) {
            logger.error(
                "Falha ao reservar os assentos. Liberando {} assento(s) já bloqueado(s). Motivo: {}",
                reservedTicketIds.size(),
                ex.getMessage()
            );

            reservedTicketIds.forEach(reservedTicketId -> {
                try {
                    releaseTicket(reservedTicketId);
                } catch (RuntimeException releaseEx) {
                    logger.error(
                        "Falha ao liberar o assento {} durante a compensação. Motivo: {}",
                        reservedTicketId,
                        releaseEx.getMessage()
                    );
                }
            });

            throw ex;
        }
    }

    @Transactional
    public void confirmTicketSold(String ticketId) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!ticketReserve.getTicketStatus()
            .equals(TicketStatusEnum.RESERVED)) {
            throw new BusinessException("Assento não reservado previamente.");
        }

        ticketReserve.sold();
        ticketReserve.removeExpiration();
        ticketRepository.save(ticketReserve);
    }

    @Transactional
    public void releaseTicket(String ticketId) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        ticketReserve.release();
        ticketRepository.save(ticketReserve);
    }

    public TicketStatusResponseDTO consultTicketStatus(String ticketId) {
        TicketReserve ticketReserve = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException("Ingresso não encontrado"));

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new TicketStatusResponseDTO(
            ticketReserve.getTicketId(),
            ticketReserve.getTicketStatus(),
            LocalDateTime.now()
                .minusSeconds(ticketReserve.getTimeToLive()),
            environment
        );
    }

    public List<TicketStatusResponseDTO> findUserRequestedTickets(String userId) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return ticketRepository.findByUserId(userId)
            .stream()
            .map(sl -> new TicketStatusResponseDTO(
                sl.getTicketId(),
                sl.getTicketStatus(),
                sl.getTicketStatus()
                    .equals(TicketStatusEnum.RESERVED) ? LocalDateTime.now()
                    .plusSeconds(sl.getTimeToLive()) : null,
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
                e.getTicketId(),
                e.getEventId(),
                e.getSectorId(),
                environment
            ))
            .toList();
    }

    public TicketResponseDTO findTicketById(String ticketId) {
        TicketReserve ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException("Assento inexistente."));

        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return new TicketResponseDTO(
            ticket.getTicketId(),
            ticket.getEventId(),
            ticket.getSectorId(),
            environment
        );
    }

    public List<TicketResponseDTO> findEventSectorTicketsByStatus(
        String eventId,
        String sectorId,
        TicketStatusEnum status
    ) {
        String environment = "PORT [INVENTORY]: " + informationService.retrieveServerPort();

        return ticketRepository.findByEventIdAndSectorIdAndStatus(
                eventId,
                sectorId,
                status
            )
            .stream()
            .map(ticket -> new TicketResponseDTO(
                ticket.getTicketId(),
                ticket.getEventId(),
                ticket.getSectorId(),
                environment
            ))
            .toList();
    }
}