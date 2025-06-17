package ru.andreevcode.proxy.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.andreevcode.proxy.client.CustomWebClient;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProxyService {
    private static final int MAX_RANDOM_VALUE = 100;
    private static final Random RANDOM = new Random();
    private final CustomWebClient monolithWebClient;
    private final CustomWebClient eventsWebClient;
    private final CustomWebClient moviesWebClient;

    @Value("${GRADUAL_MIGRATION:false}")
    private boolean gradualMigration;
    @Value("${MOVIES_MIGRATION_PERCENT:100}")
    private int moviesMigrationPercent;

    public Mono<ResponseEntity<String>> proxyRequest(
            HttpMethod method,
            String uriPath,
            HttpHeaders headers,
            WebClient client,
            @Nullable Mono<String> requestBodyMono
    ) {
        return client
                .method(method)
                .uri(uriPath)
                .headers(h -> h.addAll(headers))
                .body(requestBodyMono != null ? requestBodyMono : Mono.empty(), String.class)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> ResponseEntity
                                        .status(response.statusCode())
                                        .headers(response.headers().asHttpHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(body)
                                )
                );
    }

    public Optional<CustomWebClient> chooseDirectionClient(String path) {
        if (path.contains("/movies")) {
            return Optional.of(useMicroservice() ? moviesWebClient : monolithWebClient);
        } else if (path.contains("/users") || path.contains("/payments") || path.contains("/subscriptions")) {
            return Optional.of(monolithWebClient);
        } else if (path.contains("/events")) {
            return Optional.of(eventsWebClient);
        }
        return Optional.empty();
    }

    private boolean useMicroservice() {
        return gradualMigration && RANDOM.nextInt(MAX_RANDOM_VALUE) < moviesMigrationPercent;
    }
}
