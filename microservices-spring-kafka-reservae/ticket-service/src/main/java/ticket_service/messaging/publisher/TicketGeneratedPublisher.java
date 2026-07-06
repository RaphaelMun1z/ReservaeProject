package ticket_service.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket_service.messaging.event.TicketGeneratedEvent;

@Component
public class TicketGeneratedPublisher {

    private static final Logger logger =
        LoggerFactory.getLogger(TicketGeneratedPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ticketGeneratedTopic;

    public TicketGeneratedPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${reservae.config.kafka.topics.ingresso-gerado}") String ticketGeneratedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.ticketGeneratedTopic = ticketGeneratedTopic;
    }

    public void publish(TicketGeneratedEvent event) {
        kafkaTemplate.send(
            ticketGeneratedTopic,
            event.orderId(),
            event
        );

        logger.info(
            "Evento de ingressos gerados publicado. Pedido: {}. Tópico: {}.",
            event.orderId(),
            ticketGeneratedTopic
        );
    }
}