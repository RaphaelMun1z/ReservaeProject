package order_service.messaging.publisher;

import order_service.messaging.event.order.OrderConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OrderConfirmedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public OrderConfirmedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${reservae.config.kafka.topics.pedido-confirmado}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(OrderConfirmedEvent event) {
        kafkaTemplate.send(topic, event.orderId(), event)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error(
                        "Erro ao publicar pedido confirmado {}.",
                        event.orderId(),
                        exception
                    );
                    return;
                }

                logger.info(
                    "Pedido confirmado {} publicado no tópico {}.",
                    event.orderId(),
                    topic
                );
            });
    }
}