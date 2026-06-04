package notification_service.services;

import notification_service.dtos.req.SendNotificationRequestDTO;
import notification_service.entities.NotificationLog;
import notification_service.entities.enums.NotificationStatusEnum;
import notification_service.repositories.NotificationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final NotificationLogRepository repository;

    public NotificationService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public void processNotification(SendNotificationRequestDTO dto) {
        NotificationLog log = new NotificationLog(
            dto.userId(),
            dto.recipientAddress(),
            dto.type(),
            dto.subject(),
            dto.payload(),
            null,
            null,
            LocalDateTime.now()
        );

        try {
            simulateEmailSending(dto);

            log.changeStatus(NotificationStatusEnum.SENT);
        } catch (Exception e) {
            log.changeStatus(NotificationStatusEnum.FAILED);
            log.setErrorMessage(e.getMessage());
        } finally {
            repository.save(log);
        }
    }

    private void simulateEmailSending(SendNotificationRequestDTO dto) throws Exception {
        System.out.println("Enviando " + dto.type() + " para " + dto.recipientAddress() + "...");
        System.out.println("Enviado com sucesso!");
    }
}
