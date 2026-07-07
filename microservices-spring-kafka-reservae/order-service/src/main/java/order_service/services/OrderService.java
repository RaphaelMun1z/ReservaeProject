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
import order_service.messaging.event.inventory.InventoryReservationResultEvent;
import order_service.messaging.event.order.OrderConfirmedEvent;
import order_service.messaging.event.order.OrderConfirmedItemEvent;
import order_service.messaging.event.order.OrderReservationRequestedEvent;
import order_service.messaging.event.payment.*;
import order_service.messaging.mapper.OrderEventMapper;
import order_service.messaging.publisher.OrderConfirmedPublisher;
import order_service.messaging.publisher.PaymentConfirmedNotificationPublisher;
import order_service.messaging.publisher.PaymentFailedNotificationPublisher;
import order_service.messaging.publisher.PaymentPendingNotificationPublisher;
import order_service.proxy.event_catalog.EventCatalogProxy;
import order_service.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("LoggingSimilarMessage")
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final String CUSTOMER_NAME_MOCK = "Nome do comprador";
    private static final String CUSTOMER_EMAIL_MOCK = "raphaelmunizvarela@gmail.com";
    private static final String EVENT_NAME_MOCK = "Nome do evento";
    private static final String EVENT_DATE_MOCK = "Data do evento";
    private static final String ITEM_IMAGE_URL_MOCK = "https://cdn-icons-png.flaticon.com/512/708/708904.png";
    private static final String FRONTEND_ORDER_URL = "http://localhost:3000/orders/";
    private static final long PAYMENT_EXPIRATION_MINUTES = 30;

    private final OrderRepository orderRepository;
    private final OrderEventMapper orderEventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PaymentPendingNotificationPublisher paymentPendingNotificationPublisher;
    private final PaymentConfirmedNotificationPublisher paymentConfirmedNotificationPublisher;
    private final PaymentFailedNotificationPublisher paymentFailedNotificationPublisher;
    private final OrderConfirmedPublisher orderConfirmedPublisher;
    private final EventCatalogProxy eventCatalogProxy;

    public OrderService(
        OrderRepository orderRepository,
        OrderEventMapper orderEventMapper,
        ApplicationEventPublisher applicationEventPublisher,
        PaymentPendingNotificationPublisher paymentPendingNotificationPublisher,
        PaymentConfirmedNotificationPublisher paymentConfirmedNotificationPublisher,
        PaymentFailedNotificationPublisher paymentFailedNotificationPublisher,
        OrderConfirmedPublisher orderConfirmedPublisher,
        EventCatalogProxy eventCatalogProxy
    ) {
        this.orderRepository = orderRepository;
        this.orderEventMapper = orderEventMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.paymentPendingNotificationPublisher = paymentPendingNotificationPublisher;
        this.paymentConfirmedNotificationPublisher = paymentConfirmedNotificationPublisher;
        this.paymentFailedNotificationPublisher = paymentFailedNotificationPublisher;
        this.orderConfirmedPublisher = orderConfirmedPublisher;
        this.eventCatalogProxy = eventCatalogProxy;
    }

    @Transactional
    public OrderSummaryResponseDTO processCheckout(CheckoutRequestDTO request) {
        // Validar existência do evento e setores
        request.items().forEach(item -> {
            String eventCatalogServicePort = eventCatalogProxy.validateEventSector(
                request.eventId(),
                item.sectorId()
            );
        });

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

        OrderReservationRequestedEvent event =
            orderEventMapper.toReservationRequestedEvent(savedOrder);

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

        order.attachReservationIds(event.reservationIds());
        order.updateStatus(OrderStatusEnum.AWAITING_PAYMENT);
        orderRepository.save(order);

        logger.info(
            "Reserva confirmada para o pedido {}. Reservas: {}.",
            order.getId(),
            event.reservationIds()
        );

        publishPaymentRequest(order);
    }

    @Transactional
    public void attachPaymentSession(PaymentSessionCreatedEvent event) {
        Order order = findOrderEntityById(
            event.orderId(),
            "Nenhum pedido encontrado para vincular sessão de pagamento."
        );

        if (order.getStatus() != OrderStatusEnum.AWAITING_PAYMENT) {
            logger.warn(
                "Sessão de pagamento ignorada para o pedido {}. Status atual: {}.",
                order.getId(),
                order.getStatus()
            );
            return;
        }

        order.attachPaymentUrl(event.paymentUrl());
        orderRepository.save(order);

        publishPaymentPendingNotification(order);

        logger.info(
            "URL de pagamento vinculada ao pedido {}. Sessão: {}.",
            order.getId(),
            event.paymentId()
        );
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

        publishOrderConfirmed(order);
        publishPaymentConfirmedNotification(order);

        logger.info(
            "Pagamento confirmado para o pedido {}. PaymentIntent: {}.",
            order.getId(),
            event.paymentIntentId()
        );
    }

    private void publishOrderConfirmed(Order order) {
        List<OrderConfirmedItemEvent> items = order.getItems()
            .stream()
            .map(item -> new OrderConfirmedItemEvent(
                item.getId(),
                item.getSectorId(),
                item.getReservationId(),
                item.getTicketType().name(),
                item.getQuantity(),
                item.getAppliedPrice(),
                item.getSubtotal()
            ))
            .toList();

        OrderConfirmedEvent event = new OrderConfirmedEvent(
            java.util.UUID.randomUUID().toString(),
            order.getId(),
            order.getUserId(),
            order.getEventId(),
            order.getTotalAmount(),
            items,
            java.time.LocalDateTime.now()
        );

        orderConfirmedPublisher.publish(event);
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

        publishPaymentFailedNotification(order, event.reason());

        logger.warn(
            "Pagamento falhou para o pedido {}. Motivo: {}.",
            order.getId(),
            event.reason()
        );
    }

    @Transactional
    public OrderSummaryResponseDTO updateOrderStatus(
        String orderId,
        OrderStatusEnum orderStatusEnum
    ) {
        Order order = findOrderEntityById(
            orderId,
            "Nenhum pedido encontrado."
        );

        order.updateStatus(orderStatusEnum);
        orderRepository.save(order);

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
            order.getPaymentUrl(),
            orderItems
        );
    }

    public List<OrderSummaryResponseDTO> findOrdersByEventId(String eventId) {
        return orderRepository.findByEventId(eventId)
            .stream()
            .map(this::toOrderSummaryResponseDTO)
            .toList();
    }

    public OrderSummaryResponseDTO findOrderByReservationId(String reservationId) {
        Order order = orderRepository.findByItemsReservationId(reservationId)
            .orElseThrow(() -> new NotFoundException("Nenhum pedido encontrado."));

        return toOrderSummaryResponseDTO(order);
    }

    public List<OrderSummaryResponseDTO> findOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
            .stream()
            .map(this::toOrderSummaryResponseDTO)
            .toList();
    }

    private void publishPaymentRequest(Order order) {
        List<PaymentRequestedItemEvent> items = order.getItems()
            .stream()
            .map(item -> new PaymentRequestedItemEvent(
                buildPaymentItemName(item),
                item.getAppliedPrice()
                    .movePointRight(2)
                    .longValue(),
                (long) item.getQuantity()
            ))
            .toList();

        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(
            order.getId(),
            order.getUserId(),
            "BRL",
            items
        );

        applicationEventPublisher.publishEvent(paymentRequestedEvent);

        logger.info(
            "Solicitação de pagamento publicada para o pedido {}.",
            order.getId()
        );
    }

    private void publishPaymentPendingNotification(Order order) {
        PaymentPendingNotificationRequestedEvent event =
            new PaymentPendingNotificationRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                CUSTOMER_NAME_MOCK,
                CUSTOMER_EMAIL_MOCK,
                order.getEventId(),
                EVENT_NAME_MOCK,
                EVENT_DATE_MOCK,
                order.getTotalAmount(),
                order.getPaymentUrl(),
                buildOrderUrl(order),
                LocalDateTime.now().plusMinutes(PAYMENT_EXPIRATION_MINUTES),
                buildPaymentNotificationItems(order),
                LocalDateTime.now()
            );

        paymentPendingNotificationPublisher.publish(event);
    }

    private void publishPaymentConfirmedNotification(Order order) {
        PaymentConfirmedNotificationRequestedEvent event =
            new PaymentConfirmedNotificationRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                CUSTOMER_NAME_MOCK,
                CUSTOMER_EMAIL_MOCK,
                order.getEventId(),
                EVENT_NAME_MOCK,
                EVENT_DATE_MOCK,
                order.getTotalAmount(),
                buildOrderUrl(order),
                buildPaymentNotificationItems(order),
                LocalDateTime.now()
            );

        paymentConfirmedNotificationPublisher.publish(event);
    }

    private void publishPaymentFailedNotification(
        Order order,
        String reason
    ) {
        PaymentFailedNotificationRequestedEvent event =
            new PaymentFailedNotificationRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getUserId(),
                CUSTOMER_NAME_MOCK,
                CUSTOMER_EMAIL_MOCK,
                order.getEventId(),
                EVENT_NAME_MOCK,
                EVENT_DATE_MOCK,
                order.getTotalAmount(),
                order.getPaymentUrl(),
                buildOrderUrl(order),
                reason,
                buildPaymentNotificationItems(order),
                LocalDateTime.now()
            );

        paymentFailedNotificationPublisher.publish(event);
    }

    private List<PaymentNotificationItemEvent> buildPaymentNotificationItems(Order order) {
        return order.getItems()
            .stream()
            .map(item -> new PaymentNotificationItemEvent(
                item.getSectorId(),
                "Setor " + item.getSectorId(),
                item.getTicketType().name(),
                item.getQuantity(),
                item.getAppliedPrice(),
                item.getSubtotal(),
                ITEM_IMAGE_URL_MOCK
            ))
            .toList();
    }

    private String buildPaymentItemName(OrderItem item) {
        return "Ingresso Reservae - "
            + item.getTicketType()
            + " - Setor "
            + item.getSectorId();
    }

    private String buildOrderUrl(Order order) {
        return FRONTEND_ORDER_URL + order.getId();
    }

    private Order findOrderEntityById(String orderId, String message) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException(message));
    }

    private BigDecimal calculateTotalAmount(List<OrderItemRequestDTO> items) {
        return items.stream()
            .map(item -> item.appliedPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(
                BigDecimal.ZERO,
                BigDecimal::add
            );
    }

    private OrderItem toOrderItem(OrderItemRequestDTO item) {
        return new OrderItem(
            item.sectorId(),
            item.ticketType(),
            item.quantity(),
            item.appliedPrice()
        );
    }

    private OrderSummaryResponseDTO toOrderSummaryResponseDTO(Order order) {
        return new OrderSummaryResponseDTO(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getPaymentUrl()
        );
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
        return new OrderItemResponseDTO(
            orderItem.getId(),
            orderItem.getSectorId(),
            orderItem.getReservationId(),
            orderItem.getTicketType(),
            orderItem.getQuantity(),
            orderItem.getAppliedPrice(),
            orderItem.getSubtotal()
        );
    }
}