package order_service.messaging.publisher;

import order_service.messaging.event.payment.PaymentConfirmedNotificationRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmedNotificationPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentConfirmedNotificationPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public PaymentConfirmedNotificationPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${reservae.config.kafka.topics.notificacao-pagamento-confirmado}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(PaymentConfirmedNotificationRequestedEvent event) {
        kafkaTemplate.send(topic, event.orderId(), event)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error(
                        "Erro ao publicar notificação de pagamento confirmado do pedido {}.",
                        event.orderId(),
                        exception
                    );
                    return;
                }

                logger.info(
                    "Notificação de pagamento confirmado do pedido {} publicada no tópico {}.",
                    event.orderId(),
                    topic
                );
            });
    }
}