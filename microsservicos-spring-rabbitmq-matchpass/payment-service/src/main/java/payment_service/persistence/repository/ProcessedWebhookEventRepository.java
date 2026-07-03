package payment_service.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment_service.persistence.entity.ProcessedWebhookEvent;

public interface ProcessedWebhookEventRepository extends JpaRepository<ProcessedWebhookEvent, String> {
}