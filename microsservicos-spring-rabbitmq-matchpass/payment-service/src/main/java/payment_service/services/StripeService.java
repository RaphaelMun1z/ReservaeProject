package payment_service.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import payment_service.dtos.req.ProductRequestDTO;
import payment_service.dtos.req.SubscriptionRequestDTO;
import payment_service.dtos.res.StripeResponseDTO;

@Service
public class StripeService {
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.payment.success-url}")
    private String paymentSuccessUrl;

    @Value("${stripe.payment.cancel-url}")
    private String paymentCancelUrl;

    @Value("${stripe.subscription.monthly-price-id}")
    private String monthlyPriceId;

    @Value("${stripe.subscription.yearly-price-id}")
    private String yearlyPriceId;

    private void configureStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

    public StripeResponseDTO checkoutProducts(ProductRequestDTO req) {
        logger.info("Chegou no payment-service");
        configureStripe();

        try {
            logger.info("Entrou no try");
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(req.name()).build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder().setCurrency(req.currency() == null ? "BRL" : req.currency()).setUnitAmount(req.amount()).setProductData(productData).build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder().setQuantity(req.quantity()).setPriceData(priceData).build();

            SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(paymentSuccessUrl).setCancelUrl(paymentCancelUrl).addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                //.addPaymentMethodType(SessionCreateParams.PaymentMethodType.LINK)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.BOLETO).addLineItem(lineItem).build();

            Session session = Session.create(params);

            return new StripeResponseDTO("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException ex) {
            logger.error("Erro ao criar sessão Stripe", ex);
            throw new RuntimeException(ex);
        }
    }

    public StripeResponseDTO createSubscription(SubscriptionRequestDTO req) {
        configureStripe();

        String priceId = switch (req.planType()) {
            case MONTHLY -> monthlyPriceId;
            case YEARLY -> yearlyPriceId;
        };

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(paymentSuccessUrl)
                .setCancelUrl(paymentCancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setPrice(priceId)
                    .setQuantity(1L)
                    .build())
                .build();
            Session session = Session.create(params);
            return new StripeResponseDTO(
                "SUCCESS",
                "Subscription session created",
                session.getId(),
                session.getUrl()
            );
        } catch (StripeException ex) {
            logger.error("Erro ao criar assinatura Stripe", ex);
            throw new RuntimeException(ex);
        }
    }
}
