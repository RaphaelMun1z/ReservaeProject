package payment_service.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import payment_service.messaging.event.PaymentRequestedEvent;
import payment_service.services.PaymentService;

@Component
public class PaymentRequestedConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestedConsumer.class);

    private final PaymentService paymentService;

    public PaymentRequestedConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.pagamento-solicitado}",
        containerFactory = "paymentRequestedKafkaListenerContainerFactory"
    )
    public void consume(PaymentRequestedEvent event) {
        logger.info(
            "Solicitação de pagamento recebida. Pedido: {}. Usuário: {}. Valor: {} {}.",
            event.orderId(),
            event.userId(),
            event.amount(),
            event.currency()
        );

        paymentService.processPaymentRequest(event);
    }
}