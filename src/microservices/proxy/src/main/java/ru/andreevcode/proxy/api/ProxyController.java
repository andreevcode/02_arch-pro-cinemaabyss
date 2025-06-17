package ru.andreevcode.proxy.api;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.andreevcode.proxy.service.ProxyService;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProxyController {
    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final ProxyService proxy;

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Map.of("status", true);
    }

    @GetMapping("api/**")
    public Mono<ResponseEntity<String>> universalGet(ServerHttpRequest request) {
        return universalRequest(request, HttpMethod.GET, null);
    }

    @PostMapping("api/**")
    public Mono<ResponseEntity<String>> universalPost(
            ServerHttpRequest request,
            @RequestBody Mono<String> rawBodyMono
    ) {
        return universalRequest(request, HttpMethod.POST, rawBodyMono);
    }

    private Mono<ResponseEntity<String>> universalRequest(ServerHttpRequest request, HttpMethod method,
                                                     @RequestBody @Nullable Mono<String> rawBodyMono) {
        var pathWithQuery = getFullUri(request.getURI());
        var client = proxy.chooseDirectionClient(pathWithQuery);
        if (client.isEmpty()) {
            log.info("Proxying {} {} is not allowed", method.name(), pathWithQuery);
            return Mono.just(ResponseEntity.notFound().build());
        }

        log.info("Proxying {} request to {}{}", method.name(), client.get().baseUrl(), pathWithQuery);
        return proxy.proxyRequest(
                method,
                pathWithQuery,
                extractHeaders(request),
                client.get().webClient(),
                rawBodyMono
        );
    }

    private String getFullUri(URI uri){
        return UriComponentsBuilder
                .fromPath(uri.getRawPath())
                .query(uri.getRawQuery())
                .build(true)
                .toUriString();
    }

    private HttpHeaders extractHeaders(ServerHttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach((key, values) -> {
            for (String value : values) {
                headers.add(key, value);
            }
        });
        return headers;
    }
}
