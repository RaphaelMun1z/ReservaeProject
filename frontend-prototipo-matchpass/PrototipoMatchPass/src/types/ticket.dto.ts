import type { EventDto, EventSectorDto } from "./event.dto";

export type TicketStatusDto = "RESERVED" | "CONFIRMED" | "USED" | "CANCELED" | "TRANSFERRED";

export interface TicketDto {
  id: string;
  code: string;
  status: TicketStatusDto;
  holderName: string;
  holderDocument?: string;
  qrCode?: string;
  event: EventDto;
  sector: EventSectorDto;
  orderId?: string;
  issuedAt?: string;
}

export interface TicketTransferRequestDto {
  recipientEmail: string;
  message?: string;
}

export interface TicketValidationRequestDto {
  code: string;
  eventId?: string;
  gateId?: string;
}

export interface TicketValidationResultDto {
  valid: boolean;
  reason?: string;
  ticket?: TicketDto;
  validatedAt?: string;
}

