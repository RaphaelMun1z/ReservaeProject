package order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${matchpass.config.kafka.topics.reserva-solicitada}")
    private String reservationRequestedTopic;

    @Value("${matchpass.config.kafka.topics.resultado-reserva}")
    private String reservationResultTopic;

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
}