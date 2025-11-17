package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestOperations.class */
public interface RestOperations {
    @Nullable
    <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T getForObject(URI url, Class<T> responseType) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException;

    HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException;

    HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException;

    HttpHeaders headForHeaders(URI url) throws RestClientException;

    @Nullable
    URI postForLocation(String url, @Nullable Object request, Object... uriVariables) throws RestClientException;

    @Nullable
    URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    URI postForLocation(URI url, @Nullable Object request) throws RestClientException;

    @Nullable
    <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException;

    void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException;

    void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException;

    void put(URI url, @Nullable Object request) throws RestClientException;

    @Nullable
    <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T patchForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException;

    void delete(String url, Object... uriVariables) throws RestClientException;

    void delete(String url, Map<String, ?> uriVariables) throws RestClientException;

    void delete(URI url) throws RestClientException;

    Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException;

    Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException;

    Set<HttpMethod> optionsForAllow(URI url) throws RestClientException;

    <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException;

    <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException;

    <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException;

    <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException;

    <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException;

    @Nullable
    <T> T execute(String uriTemplate, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T execute(String uriTemplate, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T execute(URI url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException;
}
