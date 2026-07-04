package payment_service.config;

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
import payment_service.messaging.event.PaymentRequestedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${matchpass.config.kafka.server-url}")
    private String kafkaServerUrl;

    @Value("${matchpass.config.kafka.consumer-group}")
    private String consumerGroup;

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
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, PaymentRequestedEvent>
    paymentRequestedConsumerFactory() {
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

        JsonDeserializer<PaymentRequestedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentRequestedEvent.class,
                false
            );

        deserializer.addTrustedPackages(
            "payment_service.messaging.event"
        );

        return new DefaultKafkaConsumerFactory<>(
            properties,
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
        String,
        PaymentRequestedEvent
        > paymentRequestedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<
            String,
            PaymentRequestedEvent
            > factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
            paymentRequestedConsumerFactory()
        );

        return factory;
    }
}