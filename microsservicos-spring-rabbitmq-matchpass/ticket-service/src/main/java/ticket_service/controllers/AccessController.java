package ticket_service.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ticket_service.controllers.contracts.AccessContract;
import ticket_service.dtos.req.ValidateAccessRequestDTO;
import ticket_service.dtos.res.AccessValidationResponse;
import ticket_service.entities.AccessLog;
import ticket_service.services.AccessService;

@RestController
public class AccessController implements AccessContract {

    private final AccessService accessService;

    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }

    @Override
    public ResponseEntity<AccessValidationResponse> validateAccess(ValidateAccessRequestDTO dto) {
        return ResponseEntity.ok(accessService.validateTicket(dto));
    }

    @Override
    public ResponseEntity<Page<AccessLog>> getAccessLogs(
        String eventId,
        String gateId,
        String result,
        Pageable pageable
    ) {
        return ResponseEntity.ok(accessService.findLogs(eventId, gateId, result, pageable));
    }
}