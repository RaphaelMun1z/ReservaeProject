package notification_service.controllers;

import jakarta.validation.Valid;
import notification_service.dtos.req.SendNotificationRequestDTO;
import notification_service.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(
        @Valid @RequestBody SendNotificationRequestDTO dto
    ) {
        notificationService.processNotification(dto);
        return ResponseEntity.accepted().build();
    }
}