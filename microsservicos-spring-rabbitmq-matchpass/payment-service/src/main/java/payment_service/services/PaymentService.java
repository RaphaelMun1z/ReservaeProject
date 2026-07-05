package payment_service.services;

import org.springframework.stereotype.Service;
import payment_service.dtos.req.ProductRequestDTO;
import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.gateways.PaymentGateway;
import payment_service.gateways.model.PaymentSessionRequest;
import payment_service.messaging.event.PaymentFailedEvent;
import payment_service.messaging.event.PaymentRequestedEvent;
import payment_service.messaging.event.PaymentSessionCreatedEvent;
import payment_service.messaging.mapper.PaymentEventMapper;
import payment_service.messaging.publisher.PaymentFailedPublisher;
import payment_service.messaging.publisher.PaymentSessionCreatedPublisher;

import java.time.Instant;

@Service
public class PaymentService {
    private final PaymentGateway paymentGateway;
    private final PaymentEventMapper paymentEventMapper;
    private final PaymentSessionCreatedPublisher paymentSessionCreatedPublisher;
    private final PaymentFailedPublisher paymentFailedPublisher;

    public PaymentService(
        PaymentGateway paymentGateway,
        PaymentEventMapper paymentEventMapper,
        PaymentSessionCreatedPublisher paymentSessionCreatedPublisher,
        PaymentFailedPublisher paymentFailedPublisher
    ) {
        this.paymentGateway = paymentGateway;
        this.paymentEventMapper = paymentEventMapper;
        this.paymentSessionCreatedPublisher = paymentSessionCreatedPublisher;
        this.paymentFailedPublisher = paymentFailedPublisher;
    }

    public PaymentSessionResponseDTO createPaymentSession(ProductRequestDTO request) {
        validateRequest(request);

        PaymentSessionRequest paymentSessionRequest = new PaymentSessionRequest(
            request.orderId(),
            request.userId(),
            request.amount(),
            request.quantity(),
            request.name(),
            request.currency(),
            request.customerEmail()
        );

        return paymentGateway.createPaymentSession(paymentSessionRequest);
    }

    public void processPaymentRequest(PaymentRequestedEvent event) {
        validateEvent(event);

        try {
            PaymentSessionRequest request =
                paymentEventMapper.toPaymentSessionRequest(event);

            PaymentSessionResponseDTO response =
                paymentGateway.createPaymentSession(request);

            PaymentSessionCreatedEvent sessionCreatedEvent = paymentEventMapper.toPaymentSessionCreatedEvent(response);

            paymentSessionCreatedPublisher.publish(sessionCreatedEvent);
        } catch (RuntimeException exception) {
            paymentFailedPublisher.publish(
                new PaymentFailedEvent(
                    event.orderId(),
                    null,
                    "Falha ao criar a sessão de pagamento.",
                    Instant.now()
                )
            );

            throw exception;
        }
    }

    private void validateRequest(ProductRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados do pagamento são obrigatórios."
            );
        }

        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new IllegalArgumentException(
                "O identificador do pedido é obrigatório."
            );
        }

        if (request.userId() == null || request.userId().isBlank()) {
            throw new IllegalArgumentException(
                "O identificador do usuário é obrigatório."
            );
        }

        if (request.amount() == null || request.amount() <= 0) {
            throw new IllegalArgumentException(
                "O valor do pagamento deve ser maior que zero."
            );
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException(
                "A quantidade deve ser maior que zero."
            );
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException(
                "O nome do produto é obrigatório."
            );
        }

        if (request.currency() == null || request.currency().isBlank()) {
            throw new IllegalArgumentException(
                "A moeda é obrigatória."
            );
        }
    }

    private void validateEvent(PaymentRequestedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                "O evento de solicitação de pagamento é obrigatório."
            );
        }

        if (event.orderId() == null || event.orderId().isBlank()) {
            throw new IllegalArgumentException(
                "O identificador do pedido é obrigatório."
            );
        }

        if (event.userId() == null || event.userId().isBlank()) {
            throw new IllegalArgumentException(
                "O identificador do usuário é obrigatório."
            );
        }

        if (event.amount() == null || event.amount() <= 0) {
            throw new IllegalArgumentException(
                "O valor do pagamento deve ser maior que zero."
            );
        }

        if (event.quantity() == null || event.quantity() <= 0) {
            throw new IllegalArgumentException(
                "A quantidade deve ser maior que zero."
            );
        }

        if (event.productName() == null || event.productName().isBlank()) {
            throw new IllegalArgumentException(
                "O nome do produto é obrigatório."
            );
        }

        if (event.currency() == null || event.currency().isBlank()) {
            throw new IllegalArgumentException(
                "A moeda é obrigatória."
            );
        }
    }
}