package notification_service.messaging.consumer;

import notification_service.messaging.event.PaymentSessionCreatedEvent;
import notification_service.services.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSessionCreatedConsumer {

    private final NotificationService notificationService;

    public PaymentSessionCreatedConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.sessao-pagamento-criada}",
        containerFactory = "paymentSessionCreatedKafkaListenerContainerFactory"
    )
    public void consume(PaymentSessionCreatedEvent event) {
        notificationService.enviarPagamentoPendente(event);
    }
}