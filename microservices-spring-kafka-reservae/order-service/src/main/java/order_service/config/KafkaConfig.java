package order_service.config;

import order_service.messaging.event.inventory.InventoryReservationResultEvent;
import order_service.messaging.event.payment.PaymentApprovedEvent;
import order_service.messaging.event.payment.PaymentFailedEvent;
import order_service.messaging.event.payment.PaymentSessionCreatedEvent;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${reservae.config.kafka.server-url}")
    private String kafkaServerUrl;

    @Value("${reservae.config.kafka.consumer-groups.resultado-reserva}")
    private String reservationResultConsumerGroup;

    @Value("${reservae.config.kafka.consumer-groups.sessao-pagamento-criada}")
    private String paymentSessionCreatedConsumerGroup;

    @Value("${reservae.config.kafka.consumer-groups.pagamento-aprovado}")
    private String paymentApprovedConsumerGroup;

    @Value("${reservae.config.kafka.consumer-groups.pagamento-falhou}")
    private String paymentFailedConsumerGroup;

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
    public ConsumerFactory<String, InventoryReservationResultEvent>
    inventoryReservationConsumerFactory() {
        JsonDeserializer<InventoryReservationResultEvent> deserializer =
            new JsonDeserializer<>(
                InventoryReservationResultEvent.class,
                false
            );

        deserializer.addTrustedPackages("order_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(reservationResultConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservationResultEvent>
    inventoryReservationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReservationResultEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryReservationConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentSessionCreatedEvent>
    paymentSessionCreatedConsumerFactory() {
        JsonDeserializer<PaymentSessionCreatedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentSessionCreatedEvent.class,
                false
            );

        deserializer.addTrustedPackages("order_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentSessionCreatedConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSessionCreatedEvent>
    paymentSessionCreatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentSessionCreatedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentSessionCreatedConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentApprovedEvent>
    paymentApprovedConsumerFactory() {
        JsonDeserializer<PaymentApprovedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentApprovedEvent.class,
                false
            );

        deserializer.addTrustedPackages("order_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentApprovedConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentApprovedEvent>
    paymentApprovedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentApprovedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentApprovedConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentFailedEvent>
    paymentFailedConsumerFactory() {
        JsonDeserializer<PaymentFailedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentFailedEvent.class,
                false
            );

        deserializer.addTrustedPackages("order_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentFailedConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>
    paymentFailedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentFailedConsumerFactory());

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