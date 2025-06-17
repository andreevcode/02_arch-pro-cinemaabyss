package ru.andreevcode.events.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.andreevcode.events.model.Movie;
import ru.andreevcode.events.model.Payment;
import ru.andreevcode.events.model.User;

import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController {
    private static final Logger log = LoggerFactory.getLogger(EventsController.class);
    private static final String STATUS = "status";
    private static final String SUCCESS = "success";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Map.of(STATUS, true);
    }

    @PostMapping("/movie")
    public ResponseEntity<Map<String, String>> addMovie(@RequestBody Movie movie) {
        log.info("Sending received movie: {} to kafka", movie);
        kafkaTemplate.send("movie-events", movie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(STATUS, SUCCESS));
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, String>> addMovie(@RequestBody User user) {
        log.info("Sending received user: {} to kafka", user);
        kafkaTemplate.send("user-events", user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(STATUS, SUCCESS));
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> addMovie(@RequestBody Payment payment) {
        log.info("Sending received payment: {} to kafka", payment);
        kafkaTemplate.send("payment-events", payment);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(STATUS, SUCCESS));
    }
}
