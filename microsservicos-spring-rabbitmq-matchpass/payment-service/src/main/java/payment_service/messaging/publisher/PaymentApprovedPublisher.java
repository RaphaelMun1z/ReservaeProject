package payment_service.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import payment_service.messaging.event.PaymentApprovedEvent;

@Component
public class PaymentApprovedPublisher {
    private static final Logger logger =
        LoggerFactory.getLogger(PaymentApprovedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentApprovedTopic;

    public PaymentApprovedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.pagamento-aprovado}")
        String paymentApprovedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentApprovedTopic = paymentApprovedTopic;
    }

    public void publish(PaymentApprovedEvent event) {
        kafkaTemplate.send(
            paymentApprovedTopic,
            event.orderId(),
            event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                    "Erro ao publicar pagamento aprovado do pedido {}.",
                    event.orderId(),
                    exception
                );
                return;
            }

            logger.info(
                "Pagamento aprovado do pedido {} publicado no tópico {}. Partição: {}. Offset: {}.",
                event.orderId(),
                paymentApprovedTopic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
        });
    }
}