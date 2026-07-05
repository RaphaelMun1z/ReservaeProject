package order_service.messaging.publisher;

import order_service.messaging.event.payment.PaymentFailedNotificationRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedNotificationPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentFailedNotificationPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public PaymentFailedNotificationPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.notificacao-pagamento-falhou}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(PaymentFailedNotificationRequestedEvent event) {
        kafkaTemplate.send(topic, event.orderId(), event)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error(
                        "Erro ao publicar notificação de pagamento falho do pedido {}.",
                        event.orderId(),
                        exception
                    );
                    return;
                }

                logger.info(
                    "Notificação de pagamento falho do pedido {} publicada no tópico {}.",
                    event.orderId(),
                    topic
                );
            });
    }
}