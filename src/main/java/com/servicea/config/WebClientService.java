package com.servicea.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.serviceb.config.ServiceUrlConfig;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient.Builder webClientBuilder;
    private final ServiceUrlConfig urlConfig;
    
    private String getCurrentRequestToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String token = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null && !token.isEmpty()) {
                return token;
            }
        }
        return null;
    }
    public <T> Mono<T> callService(
            String serviceName,
            String path,
            HttpMethod method,
            Object body,
            Class<T> responseType
    ) {
        String baseUrl = getBaseUrl(serviceName);
        WebClient webClientInstance = webClientBuilder.baseUrl(baseUrl).build();

        String token = getCurrentRequestToken();

        WebClient.RequestHeadersSpec<?> request;

        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            WebClient.RequestBodySpec requestBodySpec = webClientInstance.method(method).uri(path);
            request = requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            if (token != null) {
                request = request.header(HttpHeaders.AUTHORIZATION, token);
            }
            request = ((RequestBodySpec) request).bodyValue(body);
        } else {
            WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClientInstance.method(method).uri(path)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            if (token != null) {
                requestHeadersSpec = requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, token);
            }
            request = requestHeadersSpec;
        }

        return request.retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error response: " + errorBody)))
                )
                .bodyToMono(responseType);
    }

    private String getBaseUrl(String serviceName) {
        return switch (serviceName) {
            case "service-a" -> urlConfig.getServiceA();
            case "service-b" -> urlConfig.getServiceB();
            default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
        };
    }
}
