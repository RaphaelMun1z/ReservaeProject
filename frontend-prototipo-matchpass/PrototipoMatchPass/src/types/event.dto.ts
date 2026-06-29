export type EventStatusDto = "SCHEDULED" | "CANCELED" | "FINISHED";

export interface SectorRequestDto {
  name: string;
  capacity: number;
  hasNumberedSeats: boolean;
}

export interface SectorResponseDto {
  sectorId?: string;
  sectorName?: string;
  hasNumberedSeats?: boolean;
}

export interface CreateVenueRequestDto {
  name: string;
  city: string;
  state: string;
  totalCapacity: number;
  sectors: SectorRequestDto[];
}

export interface VenueResponseDto {
  id?: string;
  name?: string;
  city?: string;
  state?: string;
  totalCapacity?: number;
  sectors?: SectorResponseDto[];
}

export interface TeamRequestDto {
  name?: string;
}

export interface TeamResponseDto {
  id?: string;
  name?: string;
}

export interface SectorPricingRequestDto {
  sectorId?: string;
  basePrice?: number;
  halfPrice?: number;
}

export interface CreateEventRequestDto {
  title?: string;
  eventDate?: string;
  venueId?: string;
  homeTeamId?: string;
  awayTeamId?: string;
  sectorsPricing?: SectorPricingRequestDto[];
}

export interface EventSectorDetailsDto {
  eventId?: string;
  sectorId?: string;
  sectorName?: string;
  sectorBasePrice?: number;
  sectorHalfPrice?: number;
  hasNumberedSeats?: boolean;
  totalCapacity?: number;
}

export interface EventDetailsResponseDto {
  eventId?: string;
  title?: string;
  eventDate?: string;
  status?: EventStatusDto;
  venueName?: string;
  venueCity?: string;
  venueState?: string;
  homeTeamName?: string;
  awayTeamName?: string;
  sectorsDetails?: EventSectorDetailsDto[];
}

export interface SectorPricingResponseDto {
  sectorId?: string;
  sectorName?: string;
  basePrice?: number;
  halfPrice?: number;
  hasNumberedSeats?: boolean;
}
