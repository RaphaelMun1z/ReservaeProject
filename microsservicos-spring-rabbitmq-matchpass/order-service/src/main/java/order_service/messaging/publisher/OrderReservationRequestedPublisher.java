package order_service.messaging.publisher;

import order_service.messaging.event.order.OrderReservationRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderReservationRequestedPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OrderReservationRequestedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String reservationRequestedTopic;

    public OrderReservationRequestedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.reserva-solicitada}") String reservationRequestedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.reservationRequestedTopic = reservationRequestedTopic;
    }

    public void publish(OrderReservationRequestedEvent event) {
        kafkaTemplate.send(
            reservationRequestedTopic,
            event.orderId(),
            event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                    "Erro ao publicar a solicitação de reserva do pedido {}.",
                    event.orderId(),
                    exception
                );

                return;
            }

            logger.info(
                "Solicitação de reserva do pedido {} publicada no tópico {}. Partição: {}. Offset: {}.",
                event.orderId(),
                reservationRequestedTopic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
        });
    }
}