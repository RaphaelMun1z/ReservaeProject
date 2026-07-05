package notification_service.messaging.consumer;

import notification_service.messaging.event.PaymentConfirmedNotificationRequestedEvent;
import notification_service.services.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmedNotificationConsumer {

    private final NotificationService notificationService;

    public PaymentConfirmedNotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.notificacao-pagamento-confirmado}",
        containerFactory = "paymentConfirmedNotificationKafkaListenerContainerFactory"
    )
    public void consume(PaymentConfirmedNotificationRequestedEvent event) {
        notificationService.enviarCompraConfirmada(event);
    }
}