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
import order_service.messaging.event.InventoryReservationResultEvent;
import order_service.messaging.event.OrderReservationRequestedEvent;
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
    private static final Logger logger =
        LoggerFactory.getLogger(OrderService.class);

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
        BigDecimal totalAmount = request.items()
            .stream()
            .map(OrderItemRequestDTO::appliedPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                item.seatTag(),
                item.ticketType(),
                item.appliedPrice()
            ))
            .toList();

        newOrder.addItems(newOrderItems);

        Order savedOrder = orderRepository.save(newOrder);

        OrderReservationRequestedEvent event =
            orderEventMapper.toReservationRequestedEvent(savedOrder);

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
    public void handleInventoryReservationResult(
        InventoryReservationResultEvent event
    ) {
        Order order = orderRepository.findById(event.orderId())
            .orElseThrow(
                () -> new NotFoundException(
                    "Nenhum pedido encontrado para processar o resultado da reserva."
                )
            );

        if (order.getStatus() != OrderStatusEnum.PENDING) {
            logger.warn(
                "O resultado da reserva do pedido {} foi ignorado. Status atual: {}.",
                order.getId(),
                order.getStatus()
            );
            return;
        }

        if (event.reserved()) {
            order.updateStatus(OrderStatusEnum.AWAITING_PAYMENT);

            logger.info(
                "Reserva confirmada para o pedido {}. Assentos: {}.",
                order.getId(),
                event.seatTags()
            );
        } else {
            order.updateStatus(OrderStatusEnum.RESERVATION_FAILED);

            logger.warn(
                "Reserva recusada para o pedido {}. Motivo: {}.",
                order.getId(),
                event.reason()
            );
        }

        orderRepository.save(order);
    }

    public OrderResponseDTO findProcessById(String processId) {
        Order order = orderRepository.findById(processId)
            .orElseThrow(
                () -> new NotFoundException(
                    "Nenhum pedido encontrado."
                )
            );

        List<OrderItemResponseDTO> orderItems = order.getItems()
            .stream()
            .map(orderItem -> new OrderItemResponseDTO(
                orderItem.getId(),
                orderItem.getSectorId(),
                orderItem.getSeatTag(),
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

    public List<OrderSummaryResponseDTO> findProcessByEventId(
        String eventId
    ) {
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

    public OrderSummaryResponseDTO findProcessBySeatTag(
        String seatTag
    ) {
        Order order = orderRepository.findByItemsSeatTag(seatTag)
            .orElseThrow(
                () -> new NotFoundException(
                    "Nenhum pedido encontrado."
                )
            );

        return new OrderSummaryResponseDTO(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            null
        );
    }

    @Transactional
    public OrderSummaryResponseDTO updateProcessStatus(
        String orderId,
        OrderStatusEnum orderStatusEnum
    ) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(
                () -> new NotFoundException(
                    "Nenhum pedido encontrado."
                )
            );

        order.updateStatus(orderStatusEnum);

        return new OrderSummaryResponseDTO(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            null
        );
    }

    public List<OrderSummaryResponseDTO> findOrdersByUserId(
        String userId
    ) {
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