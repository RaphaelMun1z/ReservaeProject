package notification_service.entities;

import notification_service.entities.enums.NotificationStatusEnum;
import notification_service.entities.enums.NotificationTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notification_logs")
public class NotificationLog {
    @Id
    private String id;

    private String userId;
    private String recipientAddress;
    private NotificationTypeEnum type;
    private String subject;
    private String payload;
    private NotificationStatusEnum status;
    private String errorMessage;
    private LocalDateTime sentAt;

    public NotificationLog() {
    }

    public NotificationLog(
        String userId,
        String recipientAddress,
        NotificationTypeEnum type,
        String subject,
        String payload,
        NotificationStatusEnum status,
        String errorMessage,
        LocalDateTime sentAt
    ) {
        this.userId = userId;
        this.recipientAddress = recipientAddress;
        this.type = type;
        this.subject = subject;
        this.payload = payload;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public NotificationTypeEnum getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getPayload() {
        return payload;
    }

    public NotificationStatusEnum getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    // Métodos Auxiliares
    public void changeStatus(NotificationStatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationLog nl)) return false;
        return id != null && id.equals(nl.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
