package order_service.messaging.consumer;

import order_service.messaging.event.InventoryReservationResultEvent;
import order_service.messaging.event.PaymentRequestedEvent;
import order_service.messaging.publisher.PaymentRequestedPublisher;
import order_service.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationResultConsumer {
    private final OrderService orderService;
    private final PaymentRequestedPublisher paymentRequestedPublisher;

    public InventoryReservationResultConsumer(
            OrderService orderService,
            PaymentRequestedPublisher paymentRequestedPublisher
    ) {
        this.orderService = orderService;
        this.paymentRequestedPublisher = paymentRequestedPublisher;
    }

    @KafkaListener(
            topics = "matchpass.inventory.reservation-result.v1",
            groupId = "order-service"
    )
    public void consume(InventoryReservationResultEvent event) {
        if (!event.reserved()) {
            orderService.cancelReservationFailed(
                    event.orderId(),
                    event.reason()
            );

            return;
        }

        var order = orderService.markAsAwaitingPayment(event.orderId());

        PaymentRequestedEvent paymentEvent =
                new PaymentRequestedEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getCustomerEmail(),
                        order.getTotalAmount(),
                        "BRL"
                );

        paymentRequestedPublisher.publish(paymentEvent);
    }
}