package payment_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment_service.services.StripeWebhookService;

@RestController
@RequestMapping("/payment-service/api/webhooks")
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> receiveStripeWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        stripeWebhookService.process(
            payload,
            signatureHeader
        );
        return ResponseEntity.ok().build();
    }
}
