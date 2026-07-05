package event_catalog_service.controllers;

import event_catalog_service.controllers.contracts.ValidationContract;
import event_catalog_service.services.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event-catalog-service/api/events/validate")
public class ValidationController implements ValidationContract {
    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    @GetMapping("/v1/{eventId}/exists")
    public ResponseEntity<String> validateEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(validationService.validateEvent(eventId));
    }

    @Override
    @GetMapping("/v1/{eventId}/sector/{sectorId}/exists")
    public ResponseEntity<String> validateEventSector(
        @PathVariable String eventId,
        @PathVariable String sectorId
    ) {
        return ResponseEntity.ok(validationService.validateEventSector(
            eventId,
            sectorId
        ));
    }

    @Override
    @GetMapping("/v1/{eventId}/sector/{sectorId}/validate-capacity/{ticketsAmount}")
    public ResponseEntity<String> validateSectorCapacity(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int ticketsAmount
    ) {
        return ResponseEntity.ok(validationService.validateSectorCapacity(
            eventId,
            sectorId,
            ticketsAmount
        ));
    }
}