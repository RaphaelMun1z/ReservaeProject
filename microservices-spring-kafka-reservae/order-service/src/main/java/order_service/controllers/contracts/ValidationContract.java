package order_service.controllers.contracts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface ValidationContract {
    ResponseEntity<Void> validateOrder(@PathVariable String orderId);
}
