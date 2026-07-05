package ticket_service.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ticket_service.messaging.event.OrderConfirmedEvent;
import ticket_service.services.TicketService;

@Component
public class OrderConfirmedConsumer {

    private static final Logger logger =
        LoggerFactory.getLogger(OrderConfirmedConsumer.class);

    private final TicketService ticketService;

    public OrderConfirmedConsumer(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.pedido-confirmado}",
        containerFactory = "orderConfirmedKafkaListenerContainerFactory"
    )
    public void consume(OrderConfirmedEvent event) {
        logger.info(
            "Pedido confirmado recebido para geração de ingressos. Pedido: {}. Evento: {}. Usuário: {}.",
            event.orderId(),
            event.eventId(),
            event.userId()
        );

        ticketService.generateFromConfirmedOrder(event);

        logger.info(
            "Ingressos gerados para o pedido {}.",
            event.orderId()
        );
    }
}