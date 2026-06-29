export type SeatStatusDto = "AVAILABLE" | "LOCKED" | "SOLD";

export interface SeatStatusResponseDto {
  seatTag?: string;
  status?: SeatStatusDto;
  expiresAt?: string;
  environment?: string;
}

export interface SeatResponseDto {
  seatTag?: string;
  eventId?: string;
  sectorId?: string;
  environment?: string;
}
