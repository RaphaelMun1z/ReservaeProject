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
import payment_service.gateways.model.PaymentSessionRequest;

import java.util.Locale;

@Component
public class StripePaymentGateway implements PaymentGateway {
    private static final Logger logger = LoggerFactory.getLogger(StripePaymentGateway.class);

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
        try {
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(request.productName())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(request.currency().toLowerCase(Locale.ROOT))
                            .setUnitAmount(request.amount())
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(request.quantity())
                            .setPriceData(priceData)
                            .build();

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
                            .putMetadata("orderId", request.orderId())
                            .putMetadata("userId", request.userId())
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.BOLETO)
                            .addLineItem(lineItem);

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

    private String addQueryParameter(
            String url,
            String parameter,
            String value
    ) {
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + parameter + "=" + value;
    }
}