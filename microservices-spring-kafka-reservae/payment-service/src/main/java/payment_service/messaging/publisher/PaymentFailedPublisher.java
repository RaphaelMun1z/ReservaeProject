package payment_service.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import payment_service.messaging.event.PaymentFailedEvent;

@Component
public class PaymentFailedPublisher {
    private static final Logger logger =
        LoggerFactory.getLogger(PaymentFailedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentFailedTopic;

    public PaymentFailedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.pagamento-falhou}")
        String paymentFailedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentFailedTopic = paymentFailedTopic;
    }

    public void publish(PaymentFailedEvent event) {
        kafkaTemplate.send(
            paymentFailedTopic,
            event.orderId(),
            event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                    "Erro ao publicar falha de pagamento do pedido {}.",
                    event.orderId(),
                    exception
                );
                return;
            }

            logger.info(
                "Falha de pagamento do pedido {} publicada no tópico {}. Partição: {}. Offset: {}.",
                event.orderId(),
                paymentFailedTopic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
        });
    }
}