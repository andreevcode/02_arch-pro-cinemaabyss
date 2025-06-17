package ru.andreevcode.events.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.andreevcode.events.model.Movie;
import ru.andreevcode.events.model.Payment;
import ru.andreevcode.events.model.User;

/**
    automatically listens to kafka topics via @KafkaListener configured with topic's name and factory
 **/
@Service
public class KafkaListenerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaListenerService.class);

    @KafkaListener(topics = "movie-events", containerFactory = "movieKafkaListenerContainerFactory")
    public void listenMovieEvent(ConsumerRecord<String, Movie> movieRecord) {
        log.info("Received movie from kafka: {}", movieRecord.value());
    }

    @KafkaListener(topics = "payment-events", containerFactory = "userKafkaListenerContainerFactory")
    public void listenPaymentEvent(ConsumerRecord<String, Payment> paymentRecord) {
        log.info("Received payment from kafka: {}", paymentRecord.value());
    }

    @KafkaListener(topics = "user-events", containerFactory = "userKafkaListenerContainerFactory")
    public void listenUserEvent(ConsumerRecord<String, User> userRecord) {
        log.info("Received user from kafka: {}", userRecord.value());
    }
}
