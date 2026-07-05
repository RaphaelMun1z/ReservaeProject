package notification_service.controllers;

import notification_service.controllers.contracts.NotificationContract;
import notification_service.dtos.req.SendNotificationRequestDTO;
import notification_service.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController implements NotificationContract {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public ResponseEntity<Void> sendNotification(SendNotificationRequestDTO dto) {
        //notificationService.processNotification(dto);
        return ResponseEntity.accepted().build();
    }
}