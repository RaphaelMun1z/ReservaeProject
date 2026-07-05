package payment_service.messaging.mapper;

import org.springframework.stereotype.Component;
import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.gateways.model.PaymentSessionItemRequest;
import payment_service.gateways.model.PaymentSessionRequest;
import payment_service.messaging.event.PaymentRequestedEvent;
import payment_service.messaging.event.PaymentSessionCreatedEvent;

@Component
public class PaymentEventMapper {

    public PaymentSessionRequest toPaymentSessionRequest(PaymentRequestedEvent event) {
        return new PaymentSessionRequest(
            event.orderId(),
            event.userId(),
            event.currency(),
            null,
            event.items()
                .stream()
                .map(item -> new PaymentSessionItemRequest(
                    item.name(),
                    item.unitAmount(),
                    item.quantity()
                ))
                .toList()
        );
    }

    public PaymentSessionCreatedEvent toPaymentSessionCreatedEvent(
        PaymentSessionResponseDTO response
    ) {
        return new PaymentSessionCreatedEvent(
            response.orderId(),
            response.externalPaymentId(),
            response.paymentUrl()
        );
    }
}