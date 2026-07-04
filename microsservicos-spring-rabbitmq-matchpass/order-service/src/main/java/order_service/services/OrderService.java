package order_service.services;

import order_service.dtos.req.CheckoutRequestDTO;
import order_service.dtos.req.OrderItemRequestDTO;
import order_service.dtos.res.OrderItemResponseDTO;
import order_service.dtos.res.OrderResponseDTO;
import order_service.dtos.res.OrderSummaryResponseDTO;
import order_service.entities.Order;
import order_service.entities.OrderItem;
import order_service.entities.enums.OrderStatusEnum;
import order_service.exceptions.models.NotFoundException;
import order_service.messaging.event.*;
import order_service.messaging.mapper.OrderEventMapper;
import order_service.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("LoggingSimilarMessage")
@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventMapper orderEventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderService(
        OrderRepository orderRepository,
        OrderEventMapper orderEventMapper,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderEventMapper = orderEventMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public OrderSummaryResponseDTO processCheckout(CheckoutRequestDTO request) {
        BigDecimal totalAmount = calculateTotalAmount(request.items());

        Order newOrder = new Order(
            request.eventId(),
            request.userId(),
            totalAmount,
            OrderStatusEnum.PENDING
        );

        List<OrderItem> newOrderItems = request.items()
            .stream()
            .map(this::toOrderItem)
            .toList();

        newOrder.addItems(newOrderItems);
        Order savedOrder = orderRepository.save(newOrder);

        OrderReservationRequestedEvent event = orderEventMapper.toReservationRequestedEvent(savedOrder);
        applicationEventPublisher.publishEvent(event);

        logger.info(
            "Pedido {} criado. Solicitação de reserva preparada.",
            savedOrder.getId()
        );

        return toOrderSummaryResponseDTO(savedOrder);
    }

    @Transactional
    public void handleInventoryReservationResult(InventoryReservationResultEvent event) {
        Order order = findOrderEntityById(
            event.orderId(),
            "Nenhum pedido encontrado para processar o resultado da reserva."
        );

        if (order.getStatus() != OrderStatusEnum.PENDING) {
            logger.warn(
                "O resultado da reserva do pedido {} foi ignorado. Status atual: {}.",
                order.getId(),
                order.getStatus()
            );
            return;
        }

        if (!event.reserved()) {
            order.updateStatus(OrderStatusEnum.RESERVATION_FAILED);
            orderRepository.save(order);

            logger.warn(
                "Reserva recusada para o pedido {}. Motivo: {}.",
                order.getId(),
                event.reason()
            );

            return;
        }

        order.updateStatus(OrderStatusEnum.AWAITING_PAYMENT);
        orderRepository.save(order);

        logger.info(
            "Reserva confirmada para o pedido {}. Assentos: {}.",
            order.getId(),
            event.ticketsId()
        );

        publishPaymentRequest(order);
    }

    @Transactional
    public void confirmPayment(PaymentApprovedEvent event) {
        Order order = findOrderEntityById(
            event.orderId(),
            "Nenhum pedido encontrado para confirmar pagamento."
        );

        if (order.getStatus() != OrderStatusEnum.AWAITING_PAYMENT) {
            logger.warn(
                "Pagamento aprovado ignorado para o pedido {}. Status atual: {}.",
                order.getId(),
                order.getStatus()
            );
            return;
        }

        order.updateStatus(OrderStatusEnum.CONFIRMED);
        orderRepository.save(order);

        logger.info(
            "Pagamento confirmado para o pedido {}. PaymentIntent: {}.",
            order.getId(),
            event.paymentIntentId()
        );
    }

    @Transactional
    public void failPayment(PaymentFailedEvent event) {
        Order order = findOrderEntityById(
            event.orderId(),
            "Nenhum pedido encontrado para registrar falha no pagamento."
        );

        if (order.getStatus() != OrderStatusEnum.AWAITING_PAYMENT) {
            logger.warn(
                "Falha de pagamento ignorada para o pedido {}. Status atual: {}.",
                order.getId(),
                order.getStatus()
            );
            return;
        }

        order.updateStatus(OrderStatusEnum.PAYMENT_FAILED);
        orderRepository.save(order);

        logger.warn(
            "Pagamento falhou para o pedido {}. Motivo: {}.",
            order.getId(),
            event.reason()
        );
    }

    @Transactional
    public OrderSummaryResponseDTO updateOrderStatus(String orderId, OrderStatusEnum orderStatusEnum) {
        Order order = findOrderEntityById(
            orderId,
            "Nenhum pedido encontrado."
        );

        order.updateStatus(orderStatusEnum);
        return toOrderSummaryResponseDTO(order);
    }

    public OrderResponseDTO findOrderById(String orderId) {
        Order order = findOrderEntityById(
            orderId,
            "Nenhum pedido encontrado."
        );

        List<OrderItemResponseDTO> orderItems = order.getItems()
            .stream()
            .map(this::toOrderItemResponseDTO)
            .toList();

        return new OrderResponseDTO(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            null,
            orderItems
        );
    }

    public List<OrderSummaryResponseDTO> findOrdersByEventId(String eventId) {
        List<Order> orders = orderRepository.findByEventId(eventId);

        return orders.stream()
            .map(this::toOrderSummaryResponseDTO)
            .toList();
    }

    public OrderSummaryResponseDTO findOrderByTicketId(String ticketId) {
        Order order = orderRepository.findByItemsTicketId(ticketId)
            .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado."));

        return toOrderSummaryResponseDTO(order);
    }

    public List<OrderSummaryResponseDTO> findOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
            .map(this::toOrderSummaryResponseDTO)
            .toList();
    }

    private void publishPaymentRequest(Order order) {
        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount()
                .movePointRight(2)
                .longValue(),
            (long) order.getItems()
                .size(),
            "Ingresso MatchPass",
            "BRL"
        );

        applicationEventPublisher.publishEvent(paymentRequestedEvent);

        logger.info(
            "Solicitação de pagamento publicada para o pedido {}.",
            order.getId()
        );
    }

    private Order findOrderEntityById(String orderId, String message) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException(message));
    }

    private BigDecimal calculateTotalAmount(List<OrderItemRequestDTO> items) {
        return items.stream()
            .map(OrderItemRequestDTO::appliedPrice)
            .reduce(
                BigDecimal.ZERO,
                BigDecimal::add
            );
    }

    private OrderItem toOrderItem(OrderItemRequestDTO item) {
        return new OrderItem(
            item.sectorId(),
            item.ticketId(),
            item.ticketType(),
            item.appliedPrice()
        );
    }

    private OrderSummaryResponseDTO toOrderSummaryResponseDTO(Order order) {
        return new OrderSummaryResponseDTO(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            null
        );
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
        return new OrderItemResponseDTO(
            orderItem.getId(),
            orderItem.getSectorId(),
            orderItem.getTicketId(),
            orderItem.getTicketType(),
            orderItem.getAppliedPrice()
        );
    }
}