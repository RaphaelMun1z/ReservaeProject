package inventory_service.messaging.publisher;

import inventory_service.messaging.event.InventoryReservationResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class InventoryReservationResultPublisher {
    private static final Logger logger =
        LoggerFactory.getLogger(InventoryReservationResultPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String reservationResultTopic;

    public InventoryReservationResultPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${matchpass.config.kafka.topics.resultado-reserva}")
        String reservationResultTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.reservationResultTopic = reservationResultTopic;
    }

    public void publishSuccess(String orderId, List<String> seatTags) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID().toString(),
                orderId,
                true,
                List.copyOf(seatTags),
                null,
                LocalDateTime.now()
            );

        publish(event);
    }

    public void publishFailure(
        String orderId,
        List<String> seatTags,
        String reason
    ) {
        InventoryReservationResultEvent event =
            new InventoryReservationResultEvent(
                UUID.randomUUID().toString(),
                orderId,
                false,
                List.copyOf(seatTags),
                reason,
                LocalDateTime.now()
            );

        publish(event);
    }

    private void publish(InventoryReservationResultEvent event) {
        kafkaTemplate.send(
            reservationResultTopic,
            event.orderId(),
            event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error(
                    "Erro ao publicar o resultado da reserva do pedido {}.",
                    event.orderId(),
                    exception
                );
                return;
            }

            logger.info(
                "Resultado da reserva do pedido {} publicado no tópico {}. Partição: {}. Offset: {}.",
                event.orderId(),
                reservationResultTopic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
        });
    }
}