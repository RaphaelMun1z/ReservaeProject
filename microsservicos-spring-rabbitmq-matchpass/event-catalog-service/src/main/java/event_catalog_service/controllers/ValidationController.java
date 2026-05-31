package event_catalog_service.controllers;

import event_catalog_service.services.ValidationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event/validate")
public class ValidationController {
    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/v1/{eventId}/exists")
    public void validateEvent(@PathVariable String eventId) {
        validationService.validateEvent(eventId);
    }

    @GetMapping("/v1/{eventId}/sector/{sectorId}/exists")
    public void validateEventSector(
        @PathVariable String eventId,
        @PathVariable String sectorId) {
        validationService.validateEventSector(eventId, sectorId);
    }

    @GetMapping("/v1/{eventId}/sector/{sectorId}/seats/{seatsAmount}")
    void validateEventSectorSeatCreating(
        @PathVariable String eventId,
        @PathVariable String sectorId,
        @PathVariable int seatsAmount
    ) {
        validationService.validateEventSectorSeatCreating(
            eventId,
            sectorId,
            seatsAmount
        );
    }
}
