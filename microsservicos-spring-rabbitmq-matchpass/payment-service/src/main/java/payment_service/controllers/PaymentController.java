package payment_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment_service.dtos.req.ProductRequestDTO;
import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.services.PaymentService;

@RestController
@RequestMapping("/payment-service/api/payments/v1")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentSessionResponseDTO> createPaymentSession(@Valid @RequestBody ProductRequestDTO request) {
        PaymentSessionResponseDTO response = paymentService.createPaymentSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}