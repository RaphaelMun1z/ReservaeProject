package ticket_service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ticket_service.messaging.event.OrderConfirmedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${matchpass.config.kafka.server-url}")
    private String kafkaServerUrl;

    @Value("${matchpass.config.kafka.consumer-groups.pedido-confirmado}")
    private String orderConfirmedConsumerGroup;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaServerUrl
        );

        KafkaAdmin kafkaAdmin = new KafkaAdmin(properties);
        kafkaAdmin.setFatalIfBrokerNotAvailable(false);

        return kafkaAdmin;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaServerUrl
        );
        properties.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class
        );
        properties.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            JsonSerializer.class
        );
        properties.put(
            ProducerConfig.ACKS_CONFIG,
            "all"
        );
        properties.put(
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
            true
        );
        properties.put(
            JsonSerializer.ADD_TYPE_INFO_HEADERS,
            false
        );

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
        ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, OrderConfirmedEvent>
    orderConfirmedConsumerFactory() {
        JsonDeserializer<OrderConfirmedEvent> deserializer =
            new JsonDeserializer<>(
                OrderConfirmedEvent.class,
                false
            );

        deserializer.addTrustedPackages("ticket_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(orderConfirmedConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderConfirmedEvent>
    orderConfirmedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderConfirmedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(orderConfirmedConsumerFactory());

        return factory;
    }

    private Map<String, Object> defaultConsumerProperties(String consumerGroup) {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaServerUrl
        );
        properties.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            consumerGroup
        );
        properties.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
        );
        properties.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            false
        );
        properties.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );

        return properties;
    }
}