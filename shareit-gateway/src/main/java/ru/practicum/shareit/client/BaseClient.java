package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.common.ShareItHeaders;

import java.net.URI;
import java.util.Set;

@Component
public class BaseClient {
    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "transfer-encoding",
            "connection",
            "keep-alive",
            "content-length",
            "host",
            "te",
            "trailer",
            "upgrade",
            "proxy-authenticate",
            "proxy-authorization"
    );

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BaseClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        this.restTemplate = builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .errorHandler(new NoOpResponseErrorHandler())
                .build();
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> get(String path, Long userId) {
        return exchange(path, HttpMethod.GET, null, userId, null);
    }

    public ResponseEntity<Object> get(String path, Long userId, MultiValueMap<String, String> parameters) {
        return exchange(path, HttpMethod.GET, null, userId, parameters);
    }

    public ResponseEntity<Object> post(String path, Long userId, Object body) {
        return exchange(path, HttpMethod.POST, body, userId, null);
    }

    public ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return exchange(path, HttpMethod.PATCH, body, userId, null);
    }

    public ResponseEntity<Object> patch(String path,
                                        Long userId,
                                        Object body,
                                        MultiValueMap<String, String> parameters) {
        return exchange(path, HttpMethod.PATCH, body, userId, parameters);
    }

    public ResponseEntity<Object> delete(String path, Long userId) {
        return exchange(path, HttpMethod.DELETE, null, userId, null);
    }

    private ResponseEntity<Object> exchange(String path,
                                            HttpMethod method,
                                            Object body,
                                            Long userId,
                                            MultiValueMap<String, String> parameters) {
        URI uri = buildUri(path, parameters);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers(userId));
        ResponseEntity<Object> response = restTemplate.exchange(uri, method, requestEntity, Object.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(filteredHeaders(response.getHeaders()))
                .body(response.getBody());
    }

    private URI buildUri(String path, MultiValueMap<String, String> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl).path(path);
        if (parameters != null) {
            builder.queryParams(parameters);
        }
        return builder.build().encode().toUri();
    }

    private HttpHeaders headers(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set(ShareItHeaders.USER_ID, userId.toString());
        }
        return headers;
    }

    private HttpHeaders filteredHeaders(HttpHeaders source) {
        HttpHeaders target = new HttpHeaders();

        source.forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                target.put(name, values);
            }
        });

        return target;
    }
}
