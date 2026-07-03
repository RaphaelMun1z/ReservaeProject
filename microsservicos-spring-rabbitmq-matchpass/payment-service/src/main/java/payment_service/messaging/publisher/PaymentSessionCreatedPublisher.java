package payment_service.messaging.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import payment_service.messaging.event.PaymentSessionCreatedEvent;

@Component
public class PaymentSessionCreatedPublisher {
    private static final String TOPIC = "matchpass.payment.session-created.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentSessionCreatedPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentSessionCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId(),
                event
        );
    }
}