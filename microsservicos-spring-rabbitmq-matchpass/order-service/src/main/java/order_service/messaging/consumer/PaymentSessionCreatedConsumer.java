package order_service.messaging.consumer;

import order_service.messaging.event.PaymentSessionCreatedEvent;
import order_service.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSessionCreatedConsumer {

    private static final Logger logger =
        LoggerFactory.getLogger(PaymentSessionCreatedConsumer.class);

    private final OrderService orderService;

    public PaymentSessionCreatedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.sessao-pagamento-criada}",
        containerFactory = "paymentSessionCreatedKafkaListenerContainerFactory"
    )
    public void consume(PaymentSessionCreatedEvent event) {
        logger.info(
            "Sessão de pagamento recebida no Order Service. Pedido: {}. Sessão: {}. URL: {}.",
            event.orderId(),
            event.paymentId(),
            event.paymentUrl()
        );

        orderService.attachPaymentSession(event);
    }
}