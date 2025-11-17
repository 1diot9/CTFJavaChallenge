package org.springframework.web.service.invoker;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpClientAdapter.class */
public interface HttpClientAdapter {
    Mono<Void> requestToVoid(HttpRequestValues requestValues);

    Mono<HttpHeaders> requestToHeaders(HttpRequestValues requestValues);

    <T> Mono<T> requestToBody(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    <T> Flux<T> requestToBodyFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    Mono<ResponseEntity<Void>> requestToBodilessEntity(HttpRequestValues requestValues);

    <T> Mono<ResponseEntity<T>> requestToEntity(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    <T> Mono<ResponseEntity<Flux<T>>> requestToEntityFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    default ReactorHttpExchangeAdapter asReactorExchangeAdapter() {
        return new AbstractReactorHttpExchangeAdapter() { // from class: org.springframework.web.service.invoker.HttpClientAdapter.1
            @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
            public boolean supportsRequestAttributes() {
                return true;
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public Mono<Void> exchangeForMono(HttpRequestValues values) {
                return HttpClientAdapter.this.requestToVoid(values);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public Mono<HttpHeaders> exchangeForHeadersMono(HttpRequestValues values) {
                return HttpClientAdapter.this.requestToHeaders(values);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public <T> Mono<T> exchangeForBodyMono(HttpRequestValues values, ParameterizedTypeReference<T> bodyType) {
                return HttpClientAdapter.this.requestToBody(values, bodyType);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public <T> Flux<T> exchangeForBodyFlux(HttpRequestValues values, ParameterizedTypeReference<T> bodyType) {
                return HttpClientAdapter.this.requestToBodyFlux(values, bodyType);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public Mono<ResponseEntity<Void>> exchangeForBodilessEntityMono(HttpRequestValues values) {
                return HttpClientAdapter.this.requestToBodilessEntity(values);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public <T> Mono<ResponseEntity<T>> exchangeForEntityMono(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
                return HttpClientAdapter.this.requestToEntity(requestValues, bodyType);
            }

            @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
            public <T> Mono<ResponseEntity<Flux<T>>> exchangeForEntityFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
                return HttpClientAdapter.this.requestToEntityFlux(requestValues, bodyType);
            }
        };
    }
}
