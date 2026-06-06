package payment_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment_service.dtos.req.ProductRequestDTO;
import payment_service.dtos.req.SubscriptionRequestDTO;
import payment_service.dtos.res.StripeResponseDTO;
import payment_service.services.StripeService;

@RestController
@RequestMapping("/payment-service/api/product/v1")
public class ProductCheckoutController {
    private final StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponseDTO> checkoutProducts(@RequestBody ProductRequestDTO req) {
        return ResponseEntity.ok().body(stripeService.checkoutProducts(req));
    }

    @PostMapping("/subscription")
    public ResponseEntity<StripeResponseDTO> createSubscription(@RequestBody SubscriptionRequestDTO req) {
        return ResponseEntity.ok(stripeService.createSubscription(req));
    }
}
