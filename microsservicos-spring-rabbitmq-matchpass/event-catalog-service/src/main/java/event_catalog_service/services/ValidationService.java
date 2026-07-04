package event_catalog_service.services;

import event_catalog_service.dtos.res.EventSectorDetailsDTO;
import event_catalog_service.entities.Event;
import event_catalog_service.entities.enums.EventStatusEnum;
import event_catalog_service.environment.InstanceInformationService;
import event_catalog_service.exceptions.models.BusinessException;
import event_catalog_service.exceptions.models.NotFoundException;
import event_catalog_service.repositories.EventRepository;
import event_catalog_service.repositories.EventSectorPricingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ValidationService {
    private final InstanceInformationService informationService;

    private final EventRepository eventRepository;
    private final EventSectorPricingRepository eventSectorPricingRepository;

    public ValidationService(InstanceInformationService informationService, EventRepository eventRepository, EventSectorPricingRepository eventSectorPricingRepository) {
        this.informationService = informationService;
        this.eventRepository = eventRepository;
        this.eventSectorPricingRepository = eventSectorPricingRepository;
    }

    public String validateEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (event.getStatus() == EventStatusEnum.CANCELED) {
            throw new BusinessException("Evento cancelado. Vendas não permitidas.");
        }

        if (event.getStatus() == EventStatusEnum.FINISHED) {
            throw new BusinessException("Evento finalizado. Vendas não permitidas.");
        }

        if (LocalDateTime.now()
                .isAfter(event.getEventDate())) {
            throw new BusinessException("Evento já ocorreu. Vendas não permitidas.");
        }

        return "PORT [EVENT_CATALOG_SERVICE]: " + informationService.retrieveServerPort();
    }

    public String validateEventSector(
            String eventId,
            String sectorId) {
        boolean sectorExists = eventSectorPricingRepository.existsByEvent_IdAndSectorId(
                eventId,
                sectorId
        );

        if (!sectorExists) {
            throw new NotFoundException("Setor não encontrado no evento.");
        }

        return "PORT [EVENT_CATALOG_SERVICE]: " + informationService.retrieveServerPort();
    }

    public String validateEventSectorTicketCreating(
            String eventId,
            String sectorId,
            int ticketsAmount
    ) {
        Optional<EventSectorDetailsDTO> eventSectorDetails = eventSectorPricingRepository.findEventSectorDetailsByEventIdAndSectorId(
                eventId,
                sectorId
        );

        if (eventSectorDetails.isEmpty()) {
            throw new NotFoundException("Setor não encontrado no evento.");
        }

        int totalCapacity = eventSectorDetails.get()
                .totalCapacity();
        if (totalCapacity < ticketsAmount) {
            throw new IllegalArgumentException("O setor informado possui capacidade máxima de " + totalCapacity + " assentos, enquanto voce solicitou a criação de " + ticketsAmount + " assentos");
        }

        return "PORT [EVENT_CATALOG_SERVICE]: " + informationService.retrieveServerPort();
    }
}
