import type { AddressDto, MoneyDto } from "./common.dto";

export type EventStatusDto = "DRAFT" | "PUBLISHED" | "SOLD_OUT" | "CANCELED" | "FINISHED";

export interface EventDto {
  id: string;
  slug?: string;
  name: string;
  description?: string;
  status?: EventStatusDto;
  artistName?: string;
  tourName?: string;
  startsAt: string;
  endsAt?: string;
  imageUrl?: string;
  bannerUrl?: string;
  venue: EventVenueDto;
  minPrice?: MoneyDto;
  tags?: string[];
}

export interface EventVenueDto {
  id?: string;
  name: string;
  address: AddressDto;
  latitude?: number;
  longitude?: number;
}

export interface EventSectorDto {
  id: string;
  eventId: string;
  name: string;
  description?: string;
  color?: string;
  capacity?: number;
  availableQuantity?: number;
  price: MoneyDto;
  lotName?: string;
}

export interface EventSearchParamsDto {
  q?: string;
  city?: string;
  state?: string;
  status?: EventStatusDto;
  startsAfter?: string;
  startsBefore?: string;
  page?: number;
  size?: number;
}

