package ru.andreevcode.proxy.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${MONOLITH_URL:http://monolith:8080}")
    private String monolithUrl;
    @Value("${MOVIES_SERVICE_URL:http://movies-service:8081}")
    private String moviesServiceUrl;
    @Value("${EVENTS_SERVICE_URL:http://events-service:8082}")
    private String eventsServiceUrl;

    @Bean
    public CustomWebClient monolithWebClient() {
        return new CustomWebClient(
                WebClient.builder()
                        .baseUrl(monolithUrl)
                        .build(),
                monolithUrl
        );
    }

    @Bean
    public CustomWebClient moviesWebClient() {
        return new CustomWebClient(
                WebClient.builder()
                        .baseUrl(moviesServiceUrl)
                        .build(),
                moviesServiceUrl
        );
    }

    @Bean
    public CustomWebClient eventsWebClient() {
        return new CustomWebClient(
                WebClient.builder()
                        .baseUrl(eventsServiceUrl)
                        .build(),
                eventsServiceUrl
        );
    }
}
