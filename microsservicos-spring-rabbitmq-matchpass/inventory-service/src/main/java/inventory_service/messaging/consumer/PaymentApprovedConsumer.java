package inventory_service.messaging.consumer;

import inventory_service.messaging.event.PaymentApprovedEvent;
import inventory_service.services.InventoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentApprovedConsumer {

    private static final Logger logger =
        LoggerFactory.getLogger(PaymentApprovedConsumer.class);

    private final InventoryManagementService inventoryManagementService;

    public PaymentApprovedConsumer(
        InventoryManagementService inventoryManagementService
    ) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @KafkaListener(
        topics = "${matchpass.config.kafka.topics.pagamento-aprovado}",
        containerFactory = "paymentApprovedKafkaListenerContainerFactory"
    )
    public void consume(PaymentApprovedEvent event) {
        logger.info(
            "Pagamento aprovado recebido no Inventory Service. Pedido: {}.",
            event.orderId()
        );

        inventoryManagementService.confirmOrderReservations(event.orderId());

        logger.info(
            "Reservas confirmadas como venda no Inventory Service. Pedido: {}.",
            event.orderId()
        );
    }
}