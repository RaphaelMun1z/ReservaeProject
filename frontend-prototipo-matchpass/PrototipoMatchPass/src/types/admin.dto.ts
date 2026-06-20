import type { MoneyDto } from "./common.dto";

export interface AdminDashboardSummaryDto {
  totalEvents: number;
  activeEvents: number;
  totalCustomers: number;
  totalOrders: number;
  totalRevenue: MoneyDto;
  ticketsSold: number;
}

export interface CreateAdminEventRequestDto {
  name: string;
  description?: string;
  startsAt: string;
  endsAt?: string;
  venueId?: string;
  venueName?: string;
  imageUrl?: string;
}

