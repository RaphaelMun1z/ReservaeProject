package order_service.messaging.mapper;

import order_service.entities.Order;
import order_service.messaging.event.OrderReservationItemEvent;
import order_service.messaging.event.OrderReservationRequestedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class OrderEventMapper {

    public OrderReservationRequestedEvent toReservationRequestedEvent(Order order) {
        List<OrderReservationItemEvent> items = order.getItems()
            .stream()
            .map(item -> new OrderReservationItemEvent(
                item.getSectorId(),
                item.getQuantity()
            ))
            .toList();

        return new OrderReservationRequestedEvent(
            UUID.randomUUID().toString(),
            order.getId(),
            order.getEventId(),
            order.getUserId(),
            items,
            LocalDateTime.now()
        );
    }
}