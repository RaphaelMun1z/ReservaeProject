package notification_service.config;

import notification_service.messaging.event.PaymentConfirmedNotificationRequestedEvent;
import notification_service.messaging.event.PaymentFailedNotificationRequestedEvent;
import notification_service.messaging.event.PaymentPendingNotificationRequestedEvent;
import notification_service.messaging.event.TicketGeneratedEvent;
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

    @Value("${matchpass.config.kafka.server-url}")
    private String kafkaServerUrl;

    @Value("${matchpass.config.kafka.consumer-groups.notificacao-pagamento-pendente}")
    private String paymentPendingNotificationConsumerGroup;

    @Value("${matchpass.config.kafka.consumer-groups.notificacao-pagamento-confirmado}")
    private String paymentConfirmedNotificationConsumerGroup;

    @Value("${matchpass.config.kafka.consumer-groups.notificacao-pagamento-falhou}")
    private String paymentFailedNotificationConsumerGroup;

    @Value("${matchpass.config.kafka.consumer-groups.ingresso-gerado}")
    private String ticketGeneratedConsumerGroup;

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
    public ConsumerFactory<String, PaymentPendingNotificationRequestedEvent>
    paymentPendingNotificationConsumerFactory() {
        JsonDeserializer<PaymentPendingNotificationRequestedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentPendingNotificationRequestedEvent.class,
                false
            );

        deserializer.addTrustedPackages("notification_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentPendingNotificationConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentPendingNotificationRequestedEvent>
    paymentPendingNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentPendingNotificationRequestedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentPendingNotificationConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentConfirmedNotificationRequestedEvent>
    paymentConfirmedNotificationConsumerFactory() {
        JsonDeserializer<PaymentConfirmedNotificationRequestedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentConfirmedNotificationRequestedEvent.class,
                false
            );

        deserializer.addTrustedPackages("notification_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentConfirmedNotificationConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmedNotificationRequestedEvent>
    paymentConfirmedNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmedNotificationRequestedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentConfirmedNotificationConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentFailedNotificationRequestedEvent>
    paymentFailedNotificationConsumerFactory() {
        JsonDeserializer<PaymentFailedNotificationRequestedEvent> deserializer =
            new JsonDeserializer<>(
                PaymentFailedNotificationRequestedEvent.class,
                false
            );

        deserializer.addTrustedPackages("notification_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(paymentFailedNotificationConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedNotificationRequestedEvent>
    paymentFailedNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedNotificationRequestedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentFailedNotificationConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, TicketGeneratedEvent>
    ticketGeneratedConsumerFactory() {
        JsonDeserializer<TicketGeneratedEvent> deserializer =
            new JsonDeserializer<>(
                TicketGeneratedEvent.class,
                false
            );

        deserializer.addTrustedPackages("notification_service.messaging.event");

        return new DefaultKafkaConsumerFactory<>(
            defaultConsumerProperties(ticketGeneratedConsumerGroup),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TicketGeneratedEvent>
    ticketGeneratedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TicketGeneratedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(ticketGeneratedConsumerFactory());

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