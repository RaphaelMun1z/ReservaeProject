package order_service.messaging.publisher;

import order_service.messaging.event.payment.PaymentPendingNotificationRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentPendingNotificationPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PaymentPendingNotificationPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentPendingNotificationTopic;

    public PaymentPendingNotificationPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.notificacao-pagamento-pendente}")
        String paymentPendingNotificationTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentPendingNotificationTopic = paymentPendingNotificationTopic;
    }

    public void publish(PaymentPendingNotificationRequestedEvent event) {
        kafkaTemplate.send(
                paymentPendingNotificationTopic,
                event.orderId(),
                event
            )
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error(
                        "Erro ao publicar solicitação de notificação de pagamento pendente do pedido {}.",
                        event.orderId(),
                        exception
                    );
                    return;
                }

                logger.info(
                    "Solicitação de notificação de pagamento pendente do pedido {} publicada no tópico {}. Partição: {}. Offset: {}.",
                    event.orderId(),
                    paymentPendingNotificationTopic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            });
    }
}