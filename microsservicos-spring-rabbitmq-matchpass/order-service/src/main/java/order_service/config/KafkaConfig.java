package order_service.config;

import order_service.messaging.event.InventoryReservationResultEvent;
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
    public ConsumerFactory<String, InventoryReservationResultEvent>
    inventoryReservationConsumerFactory() {
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

        JsonDeserializer<InventoryReservationResultEvent> deserializer =
            new JsonDeserializer<>(
                InventoryReservationResultEvent.class,
                false
            );

        deserializer.addTrustedPackages(
            "order_service.messaging.event"
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
        InventoryReservationResultEvent
        > inventoryReservationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<
            String,
            InventoryReservationResultEvent
            > factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
            inventoryReservationConsumerFactory()
        );

        return factory;
    }
}