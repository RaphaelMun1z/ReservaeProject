package order_service.proxy.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentProxy {
    @PostMapping("/payment-service/api/product/v1/checkout")
    StripeResponseDTO checkoutProducts(@RequestBody ProductRequestDTO req);
}
