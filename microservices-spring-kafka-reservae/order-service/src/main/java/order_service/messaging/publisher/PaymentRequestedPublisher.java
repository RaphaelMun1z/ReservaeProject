package order_service.messaging.publisher;

import order_service.messaging.event.payment.PaymentRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestedPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentRequestedTopic;

    public PaymentRequestedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${reservae.config.kafka.topics.pagamento-solicitado}") String paymentRequestedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRequestedTopic = paymentRequestedTopic;
    }

    public void publish(PaymentRequestedEvent event) {
        kafkaTemplate.send(
            paymentRequestedTopic,
            event.orderId(),
            event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                    "Erro ao publicar solicitação de pagamento do pedido {}.",
                    event.orderId(),
                    exception
                );
                return;
            }

            logger.info(
                "Solicitação de pagamento do pedido {} publicada no tópico {}. Partição: {}. Offset: {}.",
                event.orderId(),
                paymentRequestedTopic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
        });
    }
}