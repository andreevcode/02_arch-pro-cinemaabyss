package ru.andreevcode.proxy.client;

import org.springframework.web.reactive.function.client.WebClient;

public record CustomWebClient(WebClient webClient, String baseUrl) {
}
