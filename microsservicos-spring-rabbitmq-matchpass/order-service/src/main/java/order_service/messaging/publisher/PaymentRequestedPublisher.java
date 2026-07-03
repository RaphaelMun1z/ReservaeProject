package order_service.messaging.publisher;

import order_service.messaging.event.PaymentRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestedPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestedPublisher.class);
    private static final String TOPIC = "matchpass.payment.requested.v1";
    private final KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;

    public PaymentRequestedPublisher(KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentRequestedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                        "Falha ao publicar solicitação de pagamento do pedido {}.",
                        event.orderId(),
                        exception
                );
                return;
            }

            logger.info(
                    "Solicitação de pagamento do pedido {} publicada. Partição: {}. Offset: {}.",
                    event.orderId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        });
    }
}