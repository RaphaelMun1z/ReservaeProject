package order_service.messaging.consumer;

import order_service.messaging.event.PaymentFailedEvent;
import order_service.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedConsumer {
    private final OrderService orderService;

    public PaymentFailedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${matchpass.config.kafka.topics.pagamento-falhou}", containerFactory = "paymentFailedKafkaListenerContainerFactory")
    public void consume(PaymentFailedEvent event) {
        orderService.failPayment(event);
    }
}