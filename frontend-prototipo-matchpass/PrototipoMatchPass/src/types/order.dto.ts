import type { MoneyDto } from "./common.dto";

export type OrderStatusDto = "CREATED" | "PENDING_PAYMENT" | "PAID" | "CANCELED" | "EXPIRED";
export type PaymentMethodDto = "PIX" | "CREDIT_CARD" | "DEBIT_CARD";

export interface OrderItemRequestDto {
  eventId: string;
  sectorId: string;
  quantity: number;
}

export interface CreateOrderRequestDto {
  items: OrderItemRequestDto[];
  couponCode?: string;
}

export interface OrderDto {
  id: string;
  status: OrderStatusDto;
  items: OrderItemDto[];
  subtotal: MoneyDto;
  fees?: MoneyDto;
  total: MoneyDto;
  createdAt?: string;
  expiresAt?: string;
}

export interface OrderItemDto {
  eventId: string;
  sectorId: string;
  name: string;
  quantity: number;
  unitPrice: MoneyDto;
  total: MoneyDto;
}

export interface ConfirmOrderRequestDto {
  paymentMethod: PaymentMethodDto;
  paymentToken?: string;
}

