package ru.andreevcode.events.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.andreevcode.events.model.Movie;
import ru.andreevcode.events.model.Payment;
import ru.andreevcode.events.model.User;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${KAFKA_BROKERS:kafka:9092}")
    private String kafkaBrokers;

    @Bean
    public Map<String, Object> kafkaProduceProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return props;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(Map<String, Object> kafkaProduceProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProduceProperties));
    }

    @Bean
    public Map<String, Object> kafkaBaseConsumeProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Movie> movieKafkaListenerContainerFactory(
            Map<String, Object> kafkaBaseConsumeProperties
    ) {
        Map<String, Object> movieProps = new HashMap<>(kafkaBaseConsumeProperties);
        movieProps.put(ConsumerConfig.GROUP_ID_CONFIG, "movie-group");
        movieProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Movie.class.getName());

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Movie>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(movieProps));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Payment> paymentKafkaListenerContainerFactory(
            Map<String, Object> kafkaBaseConsumeProperties
    ) {
        Map<String, Object> props = new HashMap<>(kafkaBaseConsumeProperties);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Payment.class.getName());

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Payment>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerContainerFactory(
            Map<String, Object> kafkaBaseConsumeProperties
    ) {
        Map<String, Object> props = new HashMap<>(kafkaBaseConsumeProperties);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-group");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, User.class.getName());

        var factory = new ConcurrentKafkaListenerContainerFactory<String, User>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return factory;
    }
}
