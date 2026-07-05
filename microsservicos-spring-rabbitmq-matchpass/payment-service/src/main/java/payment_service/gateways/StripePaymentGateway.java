package payment_service.gateways;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.gateways.model.PaymentSessionItemRequest;
import payment_service.gateways.model.PaymentSessionRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Component
public class StripePaymentGateway implements PaymentGateway {

    private static final Logger logger =
        LoggerFactory.getLogger(StripePaymentGateway.class);

    private final RequestOptions requestOptions;
    private final String paymentSuccessUrl;
    private final String paymentCancelUrl;

    public StripePaymentGateway(
        @Value("${stripe.secret-key}") String stripeSecretKey,
        @Value("${stripe.payment.success-url}") String paymentSuccessUrl,
        @Value("${stripe.payment.cancel-url}") String paymentCancelUrl
    ) {
        this.requestOptions = RequestOptions.builder()
            .setApiKey(stripeSecretKey)
            .build();

        this.paymentSuccessUrl = paymentSuccessUrl;
        this.paymentCancelUrl = paymentCancelUrl;
    }

    @Override
    public PaymentSessionResponseDTO createPaymentSession(PaymentSessionRequest request) {
        validateRequest(request);

        try {
            List<SessionCreateParams.LineItem> lineItems = request.items()
                .stream()
                .map(item -> toStripeLineItem(
                    item,
                    request.currency()
                ))
                .toList();

            String successUrl = addQueryParameter(
                paymentSuccessUrl,
                "orderId",
                request.orderId()
            ) + "&sessionId={CHECKOUT_SESSION_ID}";

            String cancelUrl = addQueryParameter(
                paymentCancelUrl,
                "orderId",
                request.orderId()
            );

            SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setClientReferenceId(request.orderId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setExpiresAt(calculateExpirationTimestamp())
                    .putMetadata(
                        "orderId",
                        request.orderId()
                    )
                    .putMetadata(
                        "userId",
                        request.userId()
                    )
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.BOLETO)
                    .addAllLineItem(lineItems);

            if (request.customerEmail() != null
                && !request.customerEmail().isBlank()) {
                paramsBuilder.setCustomerEmail(request.customerEmail());
            }

            Session session = Session.create(
                paramsBuilder.build(),
                requestOptions
            );

            logger.info(
                "Sessão de pagamento criada na Stripe. Pedido: {}. Sessão: {}.",
                request.orderId(),
                session.getId()
            );

            return new PaymentSessionResponseDTO(
                request.orderId(),
                session.getId(),
                session.getUrl()
            );
        } catch (StripeException exception) {
            logger.error(
                "Erro ao criar sessão de pagamento na Stripe para o pedido {}.",
                request.orderId(),
                exception
            );

            throw new IllegalStateException(
                "Não foi possível criar a sessão de pagamento.",
                exception
            );
        }
    }

    private Long calculateExpirationTimestamp() {
        return Instant.now()
            .plus(Duration.ofMinutes(30))
            .getEpochSecond();
    }

    private SessionCreateParams.LineItem toStripeLineItem(
        PaymentSessionItemRequest item,
        String currency
    ) {
        SessionCreateParams.LineItem.PriceData.ProductData productData =
            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.name())
                .build();

        SessionCreateParams.LineItem.PriceData priceData =
            SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(currency.toLowerCase(Locale.ROOT))
                .setUnitAmount(item.unitAmount())
                .setProductData(productData)
                .build();

        return SessionCreateParams.LineItem.builder()
            .setQuantity(item.quantity())
            .setPriceData(priceData)
            .build();
    }

    private void validateRequest(PaymentSessionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da sessão de pagamento são obrigatórios."
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

        if (request.currency() == null || request.currency().isBlank()) {
            throw new IllegalArgumentException(
                "A moeda do pagamento é obrigatória."
            );
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException(
                "A sessão de pagamento deve possuir ao menos um item."
            );
        }

        for (PaymentSessionItemRequest item : request.items()) {
            validateItem(item);
        }
    }

    private void validateItem(PaymentSessionItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException(
                "O item da sessão de pagamento é obrigatório."
            );
        }

        if (item.name() == null || item.name().isBlank()) {
            throw new IllegalArgumentException(
                "O nome do item de pagamento é obrigatório."
            );
        }

        if (item.unitAmount() == null || item.unitAmount() <= 0) {
            throw new IllegalArgumentException(
                "O valor unitário do item de pagamento deve ser maior que zero."
            );
        }

        if (item.quantity() == null || item.quantity() <= 0) {
            throw new IllegalArgumentException(
                "A quantidade do item de pagamento deve ser maior que zero."
            );
        }
    }

    private String addQueryParameter(
        String url,
        String parameter,
        String value
    ) {
        String separator = url.contains("?") ? "&" : "?";

        return url + separator + parameter + "=" + value;
    }
}