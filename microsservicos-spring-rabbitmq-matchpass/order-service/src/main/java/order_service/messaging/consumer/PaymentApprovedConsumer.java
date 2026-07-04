package order_service.messaging.consumer;

import order_service.messaging.event.PaymentApprovedEvent;
import order_service.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentApprovedConsumer {
    private final OrderService orderService;

    public PaymentApprovedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${matchpass.config.kafka.topics.pagamento-aprovado}", containerFactory = "paymentApprovedKafkaListenerContainerFactory")
    public void consume(PaymentApprovedEvent event) {
        orderService.confirmPayment(event);
    }
}