package notification_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notification_service.dtos.req.SendNotificationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Notification Endpoint", description = "Serviço responsável pelo processamento e envio de notificações")
@RequestMapping("/notification-service/api/notifications")
public interface NotificationContract {

    @Operation(summary = "Enviar uma nova notificação")
    @PostMapping("/send")
    ResponseEntity<Void> sendNotification(@Valid @RequestBody SendNotificationRequestDTO dto);
}