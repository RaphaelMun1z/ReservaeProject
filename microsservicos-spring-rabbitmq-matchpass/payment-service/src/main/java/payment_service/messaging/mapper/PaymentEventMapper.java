package payment_service.messaging.mapper;

import org.springframework.stereotype.Component;
import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.gateways.model.PaymentSessionRequest;
import payment_service.messaging.event.PaymentRequestedEvent;
import payment_service.messaging.event.PaymentSessionCreatedEvent;

@Component
public class PaymentEventMapper {

    public PaymentSessionRequest toPaymentSessionRequest(PaymentRequestedEvent event) {
        return new PaymentSessionRequest(
            event.orderId(),
            event.userId(),
            event.amount(),
            event.quantity(),
            event.productName(),
            event.currency(),
            null
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