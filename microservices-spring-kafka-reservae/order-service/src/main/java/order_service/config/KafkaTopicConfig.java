package order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${reservae.config.kafka.topics.reserva-solicitada}")
    private String reservationRequestedTopic;

    @Value("${reservae.config.kafka.topics.resultado-reserva}")
    private String reservationResultTopic;

    @Value("${reservae.config.kafka.topics.pagamento-solicitado}")
    private String paymentRequestedTopic;

    @Value("${reservae.config.kafka.topics.pagamento-aprovado}")
    private String paymentApprovedTopic;

    @Value("${reservae.config.kafka.topics.pagamento-falhou}")
    private String paymentFailedTopic;

    @Bean
    public NewTopic reservationRequestedTopic() {
        return TopicBuilder
            .name(reservationRequestedTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic reservationResultTopic() {
        return TopicBuilder
            .name(reservationResultTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentRequestedTopic() {
        return TopicBuilder
            .name(paymentRequestedTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentApprovedTopic() {
        return TopicBuilder
            .name(paymentApprovedTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder
            .name(paymentFailedTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }
}