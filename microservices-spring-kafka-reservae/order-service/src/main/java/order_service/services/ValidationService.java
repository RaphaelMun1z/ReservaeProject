package order_service.services;

import order_service.exceptions.models.BusinessException;
import order_service.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private final OrderRepository orderRepository;

    public ValidationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void validateOrder(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new BusinessException("O ID do pedido é obrigatório.");
        }

        boolean orderExists = orderRepository.existsById(orderId);

        if (!orderExists) {
            throw new BusinessException("O pedido informado não existe.");
        }
    }
}
