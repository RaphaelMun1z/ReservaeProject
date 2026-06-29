export type TicketStatusDto = "VALID" | "USED" | "REVOKED";
export type AccessResultDto = "GRANTED" | "DENIED_USED" | "DENIED_REVOKED" | "DENIED_INVALID";

export interface GenerateTicketRequestDto {
  orderId: string;
  eventId: string;
  userId: string;
  sectorId: string;
  seatTags: string[];
}

export interface TicketDto {
  id?: string;
  orderId?: string;
  eventId?: string;
  userId?: string;
  sectorId?: string;
  seatTag?: string;
  qrCodeHash?: string;
  status?: TicketStatusDto;
}

export interface ValidateAccessRequestDto {
  qrCodeHash?: string;
  gateId?: string;
}

export interface AccessValidationResponseDto {
  isAllowed?: boolean;
  result?: AccessResultDto;
  message?: string;
  sectorName?: string;
  seatTag?: string;
}

export interface PageableDto {
  page?: number;
  size?: number;
  sort?: string[];
}

export interface AccessLogDto {
  id?: string;
  ticketId?: string;
  gateId?: string;
  accessedAt?: string;
  result?: AccessResultDto;
}

export interface SpringPageDto<T> {
  totalElements?: number;
  totalPages?: number;
  size?: number;
  content?: T[];
  number?: number;
  first?: boolean;
  last?: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

export type PageTicketDto = SpringPageDto<TicketDto>;
export type PageAccessLogDto = SpringPageDto<AccessLogDto>;

/** @deprecated Use ValidateAccessRequestDto. */
export type TicketValidationRequestDto = ValidateAccessRequestDto;
/** @deprecated Use AccessValidationResponseDto. */
export type TicketValidationResultDto = AccessValidationResponseDto;
