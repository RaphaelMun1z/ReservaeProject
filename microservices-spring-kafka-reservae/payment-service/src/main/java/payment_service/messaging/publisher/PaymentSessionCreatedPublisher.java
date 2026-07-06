package payment_service.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import payment_service.messaging.event.PaymentSessionCreatedEvent;

@Component
public class PaymentSessionCreatedPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentSessionCreatedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentSessionCreatedTopic;

    public PaymentSessionCreatedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${reservae.config.kafka.topics.sessao-pagamento-criada}")
        String paymentSessionCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentSessionCreatedTopic = paymentSessionCreatedTopic;
    }

    public void publish(PaymentSessionCreatedEvent event) {
        kafkaTemplate.send(
                paymentSessionCreatedTopic,
                event.orderId(),
                event
            )
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error(
                        "Erro ao publicar sessão de pagamento criada para o pedido {}.",
                        event.orderId(),
                        exception
                    );
                    return;
                }

                logger.info(
                    "Sessão de pagamento criada do pedido {} publicada no tópico {}. Partição: {}. Offset: {}.",
                    event.orderId(),
                    paymentSessionCreatedTopic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            });
    }
}