export type OrderStatusDto = "PENDING" | "AWAITING_PAYMENT" | "COMPLETED" | "CANCELED" | "REFUNDED";
export type TicketTypeDto =
  | "FULL_TICKET_PRICE"
  | "HALF_TICKET_PRICE_STUDENT"
  | "HALF_TICKET_PRICE_SOLIDARITY";

export interface OrderItemRequestDto {
  sectorId?: string;
  seatTag?: string;
  ticketType?: TicketTypeDto;
  appliedPrice?: number;
}

export interface CheckoutRequestDto {
  userId?: string;
  eventId?: string;
  items?: OrderItemRequestDto[];
}

export interface OrderSummaryResponseDto {
  orderId?: string;
  totalAmount?: number;
  status?: OrderStatusDto;
  paymentUrl?: string;
}

export interface OrderItemResponseDto {
  orderItemId?: string;
  sectorId?: string;
  seatTag?: string;
  ticketType?: TicketTypeDto;
  appliedPrice?: number;
}

export interface OrderResponseDto extends OrderSummaryResponseDto {
  /** Campo retornado com esta grafia pela API. */
  itens?: OrderItemResponseDto[];
}

/** @deprecated Use CheckoutRequestDto. */
export type CreateOrderRequestDto = CheckoutRequestDto;
/** @deprecated Atualize o pedido por OrderStatusDto. */
export type ConfirmOrderRequestDto = OrderStatusDto;
