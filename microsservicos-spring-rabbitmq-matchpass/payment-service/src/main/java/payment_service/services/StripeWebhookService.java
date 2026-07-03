package payment_service.services;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import payment_service.messaging.event.PaymentApprovedEvent;
import payment_service.messaging.event.PaymentFailedEvent;
import payment_service.messaging.publisher.PaymentApprovedPublisher;
import payment_service.messaging.publisher.PaymentFailedPublisher;
import payment_service.persistence.entity.ProcessedWebhookEvent;
import payment_service.persistence.repository.ProcessedWebhookEventRepository;

import java.time.Instant;
import java.util.Locale;

@Service
public class StripeWebhookService {
    private static final Logger logger =
            LoggerFactory.getLogger(StripeWebhookService.class);

    private final String webhookSecret;
    private final ProcessedWebhookEventRepository processedEventRepository;
    private final PaymentApprovedPublisher paymentApprovedPublisher;
    private final PaymentFailedPublisher paymentFailedPublisher;

    public StripeWebhookService(
            @Value("${stripe.webhook-secret}") String webhookSecret,
            ProcessedWebhookEventRepository processedEventRepository,
            PaymentApprovedPublisher paymentApprovedPublisher,
            PaymentFailedPublisher paymentFailedPublisher
    ) {
        this.webhookSecret = webhookSecret;
        this.processedEventRepository = processedEventRepository;
        this.paymentApprovedPublisher = paymentApprovedPublisher;
        this.paymentFailedPublisher = paymentFailedPublisher;
    }

    public void process(
            String payload,
            String signatureHeader
    ) {
        Event event = constructEvent(
                payload,
                signatureHeader
        );

        if (processedEventRepository.existsById(event.getId())) {
            logger.info(
                    "Webhook Stripe já processado. Evento: {}. Tipo: {}.",
                    event.getId(),
                    event.getType()
            );

            return;
        }

        processEvent(event);
        markAsProcessed(event);
    }

    private void processEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(
                    extractSession(event),
                    eventOccurredAt(event)
            );

            case "checkout.session.async_payment_succeeded" -> publishApproved(
                    extractSession(event),
                    eventOccurredAt(event)
            );

            case "checkout.session.async_payment_failed" -> publishFailed(
                    extractSession(event),
                    "Pagamento assíncrono recusado pela Stripe.",
                    eventOccurredAt(event)
            );

            case "checkout.session.expired" -> publishFailed(
                    extractSession(event),
                    "A sessão de pagamento expirou.",
                    eventOccurredAt(event)
            );

            default -> logger.debug(
                    "Evento Stripe ignorado. Evento: {}. Tipo: {}.",
                    event.getId(),
                    event.getType()
            );
        }
    }

    private Event constructEvent(
            String payload,
            String signatureHeader
    ) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException(
                    "O corpo do webhook Stripe é obrigatório."
            );
        }

        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new IllegalArgumentException(
                    "O cabeçalho Stripe-Signature é obrigatório."
            );
        }

        try {
            return Webhook.constructEvent(
                    payload,
                    signatureHeader,
                    webhookSecret
            );
        } catch (SignatureVerificationException exception) {
            throw new IllegalArgumentException(
                    "Assinatura do webhook Stripe inválida.",
                    exception
            );
        }
    }

    private Session extractSession(Event event) {
        StripeObject object = event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não foi possível desserializar a Checkout Session."
                ));

        if (!(object instanceof Session session)) {
            throw new IllegalArgumentException(
                    "O objeto recebido no webhook não é uma Checkout Session."
            );
        }

        return session;
    }

    private void handleCheckoutCompleted(
            Session session,
            Instant occurredAt
    ) {
        String paymentStatus = session.getPaymentStatus();

        if ("paid".equals(paymentStatus)
                || "no_payment_required".equals(paymentStatus)) {
            publishApproved(
                    session,
                    occurredAt
            );

            return;
        }

        logger.info(
                "Checkout concluído, mas pagamento ainda pendente. Pedido: {}. Sessão: {}. Status: {}.",
                extractOrderId(session),
                session.getId(),
                paymentStatus
        );
    }

    private void publishApproved(
            Session session,
            Instant occurredAt
    ) {
        String orderId = extractOrderId(session);

        String currency = session.getCurrency() == null
                ? null
                : session.getCurrency().toUpperCase(Locale.ROOT);

        PaymentApprovedEvent event = new PaymentApprovedEvent(
                orderId,
                session.getId(),
                session.getPaymentIntent(),
                session.getAmountTotal(),
                currency,
                occurredAt
        );

        paymentApprovedPublisher.publish(event);

        logger.info(
                "Pagamento aprovado processado. Pedido: {}. Sessão: {}.",
                orderId,
                session.getId()
        );
    }

    private void publishFailed(
            Session session,
            String reason,
            Instant occurredAt
    ) {
        String orderId = extractOrderId(session);

        PaymentFailedEvent event = new PaymentFailedEvent(
                orderId,
                session.getId(),
                reason,
                occurredAt
        );

        paymentFailedPublisher.publish(event);

        logger.warn(
                "Falha de pagamento processada. Pedido: {}. Sessão: {}. Motivo: {}.",
                orderId,
                session.getId(),
                reason
        );
    }

    private String extractOrderId(Session session) {
        String orderId = session.getMetadata() == null
                ? null
                : session.getMetadata().get("orderId");

        if (orderId == null || orderId.isBlank()) {
            orderId = session.getClientReferenceId();
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException(
                    "A Checkout Session não contém o orderId."
            );
        }

        return orderId;
    }

    private Instant eventOccurredAt(Event event) {
        if (event.getCreated() == null) {
            return Instant.now();
        }

        return Instant.ofEpochSecond(
                event.getCreated()
        );
    }

    private void markAsProcessed(Event event) {
        try {
            processedEventRepository.saveAndFlush(
                    new ProcessedWebhookEvent(
                            event.getId(),
                            event.getType(),
                            Instant.now()
                    )
            );
        } catch (DataIntegrityViolationException exception) {
            logger.info(
                    "Webhook Stripe processado simultaneamente por outra execução. Evento: {}.",
                    event.getId()
            );
        }
    }
}