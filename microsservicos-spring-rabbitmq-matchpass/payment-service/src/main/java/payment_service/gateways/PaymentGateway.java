package payment_service.gateways;

import payment_service.dtos.res.PaymentSessionResponseDTO;
import payment_service.gateways.model.PaymentSessionRequest;

public interface PaymentGateway {
    PaymentSessionResponseDTO createPaymentSession(
            PaymentSessionRequest request
    );
}