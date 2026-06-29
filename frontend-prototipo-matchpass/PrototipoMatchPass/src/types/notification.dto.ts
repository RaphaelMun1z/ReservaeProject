export type NotificationTypeDto = "EMAIL" | "SMS" | "PUSH";

export interface SendNotificationRequestDto {
  userId: string;
  recipientAddress: string;
  type: NotificationTypeDto;
  subject: string;
  payload: string;
}
