package order_service.messaging.consumer;

import order_service.messaging.event.PaymentSessionCreatedEvent;
import order_service.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSessionCreatedConsumer {

    private final OrderService orderService;

    public PaymentSessionCreatedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.sessao-pagamento-criada}",
        containerFactory = "paymentSessionCreatedKafkaListenerContainerFactory"
    )
    public void consume(PaymentSessionCreatedEvent event) {
        orderService.attachPaymentSession(event);
    }
}