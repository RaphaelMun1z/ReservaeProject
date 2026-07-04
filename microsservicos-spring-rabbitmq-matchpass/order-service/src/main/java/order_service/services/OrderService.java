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
import order_service.messaging.publisher.PaymentRequestedPublisher;
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
    private final PaymentRequestedPublisher paymentRequestedPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventMapper orderEventMapper, ApplicationEventPublisher applicationEventPublisher, PaymentRequestedPublisher paymentRequestedPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventMapper = orderEventMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.paymentRequestedPublisher = paymentRequestedPublisher;
    }

    @Transactional
    public OrderSummaryResponseDTO processCheckout(CheckoutRequestDTO request) {
        BigDecimal totalAmount = request.items()
                .stream()
                .map(OrderItemRequestDTO::appliedPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        Order newOrder = new Order(
                request.eventId(),
                request.userId(),
                totalAmount,
                OrderStatusEnum.PENDING
        );

        List<OrderItem> newOrderItems = request.items()
                .stream()
                .map(item -> new OrderItem(
                        item.sectorId(),
                        item.ticketTag(),
                        item.ticketType(),
                        item.appliedPrice()
                ))
                .toList();

        newOrder.addItems(newOrderItems);
        Order savedOrder = orderRepository.save(newOrder);

        OrderReservationRequestedEvent event = orderEventMapper.toReservationRequestedEvent(savedOrder);
        applicationEventPublisher.publishEvent(event);
        logger.info(
                "Pedido {} criado. Solicitação de reserva preparada.",
                savedOrder.getId()
        );

        return new OrderSummaryResponseDTO(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                null
        );
    }

    @Transactional
    public void handleInventoryReservationResult(InventoryReservationResultEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado para processar o resultado da reserva."));

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
                event.ticketTags()
        );

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

        paymentRequestedPublisher.publish(paymentRequestedEvent);

        logger.info(
                "Solicitação de pagamento publicada para o pedido {}.",
                order.getId()
        );
    }

    @Transactional
    public OrderSummaryResponseDTO requestPayment(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado para iniciar pagamento."));

        if (order.getStatus() != OrderStatusEnum.AWAITING_PAYMENT) {
            throw new IllegalStateException("Pedido não está disponível para pagamento.");
        }

        logger.info(
                "Solicitação de pagamento preparada para o pedido {}.",
                order.getId()
        );

        return new OrderSummaryResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                null
        );
    }

    @Transactional
    public void confirmPayment(PaymentApprovedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado para confirmar pagamento."));

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
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado para registrar falha no pagamento."));

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

    public OrderResponseDTO findProcessById(String processId) {
        Order order = orderRepository.findById(processId)
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado."));

        List<OrderItemResponseDTO> orderItems = order.getItems()
                .stream()
                .map(orderItem -> new OrderItemResponseDTO(
                        orderItem.getId(),
                        orderItem.getSectorId(),
                        orderItem.getTicketTag(),
                        orderItem.getTicketType(),
                        orderItem.getAppliedPrice()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                null,
                orderItems
        );
    }

    public List<OrderSummaryResponseDTO> findProcessByEventId(String eventId) {
        List<Order> orders = orderRepository.findByEventId(eventId);

        return orders.stream()
                .map(order -> new OrderSummaryResponseDTO(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        null
                ))
                .toList();
    }

    public OrderSummaryResponseDTO findProcessByTicketTag(String ticketTag) {
        Order order = orderRepository.findByItemsTicketTag(ticketTag)
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado."));

        return new OrderSummaryResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                null
        );
    }

    @Transactional
    public OrderSummaryResponseDTO updateProcessStatus(String orderId, OrderStatusEnum orderStatusEnum) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado."));

        order.updateStatus(orderStatusEnum);

        return new OrderSummaryResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                null
        );
    }

    public List<OrderSummaryResponseDTO> findOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(order -> new OrderSummaryResponseDTO(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        null
                ))
                .toList();
    }
}