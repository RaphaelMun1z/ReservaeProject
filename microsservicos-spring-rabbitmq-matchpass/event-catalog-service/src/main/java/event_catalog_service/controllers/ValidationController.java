package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.ValidationContract;
import event_catalog_service.services.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController implements ValidationContract {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public ResponseEntity<String> validateEvent(String eventId) {
        return ResponseEntity.ok(validationService.validateEvent(eventId));
    }

    @Override
    public ResponseEntity<String> validateEventSector(String eventId, String sectorId) {
        return ResponseEntity.ok(validationService.validateEventSector(
                eventId,
                sectorId
        ));
    }

    @Override
    public ResponseEntity<String> validateEventSectorTicketCreating(String eventId, String sectorId, int ticketsAmount) {
        return ResponseEntity.ok(validationService.validateEventSectorTicketCreating(
                eventId,
                sectorId,
                ticketsAmount
        ));
    }
}