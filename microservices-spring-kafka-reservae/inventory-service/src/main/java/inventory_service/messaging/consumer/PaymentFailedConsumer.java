package inventory_service.messaging.consumer;

import inventory_service.messaging.event.PaymentFailedEvent;
import inventory_service.services.InventoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedConsumer {

    private static final Logger logger =
        LoggerFactory.getLogger(PaymentFailedConsumer.class);

    private final InventoryManagementService inventoryManagementService;

    public PaymentFailedConsumer(
        InventoryManagementService inventoryManagementService
    ) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @KafkaListener(
        topics = "${reservae.config.kafka.topics.pagamento-falhou}",
        containerFactory = "paymentFailedKafkaListenerContainerFactory"
    )
    public void consume(PaymentFailedEvent event) {
        logger.info(
            "Falha de pagamento recebida no Inventory Service. Pedido: {}. Motivo: {}.",
            event.orderId(),
            event.reason()
        );

        inventoryManagementService.releaseOrderReservations(event.orderId());

        logger.info(
            "Reservas liberadas no Inventory Service. Pedido: {}.",
            event.orderId()
        );
    }
}