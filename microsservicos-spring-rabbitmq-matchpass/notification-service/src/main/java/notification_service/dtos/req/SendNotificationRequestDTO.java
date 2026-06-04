package notification_service.dtos.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import notification_service.entities.enums.NotificationTypeEnum;

public record SendNotificationRequestDTO(
    @NotBlank String userId,
    @NotBlank @Email String recipientAddress,
    @NotNull NotificationTypeEnum type,
    @NotBlank String subject,
    @NotBlank String payload
) {
}
