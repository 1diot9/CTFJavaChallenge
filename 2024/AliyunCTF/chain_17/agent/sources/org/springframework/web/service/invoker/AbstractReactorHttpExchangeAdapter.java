package org.springframework.web.service.invoker;

import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/AbstractReactorHttpExchangeAdapter.class */
public abstract class AbstractReactorHttpExchangeAdapter implements ReactorHttpExchangeAdapter, HttpClientAdapter {
    private ReactiveAdapterRegistry reactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance();

    @Nullable
    private Duration blockTimeout;

    public void setReactiveAdapterRegistry(ReactiveAdapterRegistry reactiveAdapterRegistry) {
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }

    @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
    public ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        return this.reactiveAdapterRegistry;
    }

    public void setBlockTimeout(@Nullable Duration blockTimeout) {
        this.blockTimeout = blockTimeout;
    }

    @Override // org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
    @Nullable
    public Duration getBlockTimeout() {
        return this.blockTimeout;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public void exchange(HttpRequestValues requestValues) {
        if (this.blockTimeout != null) {
            exchangeForMono(requestValues).block(this.blockTimeout);
        } else {
            exchangeForMono(requestValues).block();
        }
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public HttpHeaders exchangeForHeaders(HttpRequestValues requestValues) {
        HttpHeaders httpHeaders;
        if (this.blockTimeout != null) {
            httpHeaders = (HttpHeaders) exchangeForHeadersMono(requestValues).block(this.blockTimeout);
        } else {
            httpHeaders = (HttpHeaders) exchangeForHeadersMono(requestValues).block();
        }
        HttpHeaders headers = httpHeaders;
        Assert.state(headers != null, "Expected HttpHeaders");
        return headers;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> T exchangeForBody(HttpRequestValues httpRequestValues, ParameterizedTypeReference<T> parameterizedTypeReference) {
        if (this.blockTimeout != null) {
            return (T) exchangeForBodyMono(httpRequestValues, parameterizedTypeReference).block(this.blockTimeout);
        }
        return (T) exchangeForBodyMono(httpRequestValues, parameterizedTypeReference).block();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public ResponseEntity<Void> exchangeForBodilessEntity(HttpRequestValues requestValues) {
        ResponseEntity<Void> responseEntity;
        if (this.blockTimeout != null) {
            responseEntity = (ResponseEntity) exchangeForBodilessEntityMono(requestValues).block(this.blockTimeout);
        } else {
            responseEntity = (ResponseEntity) exchangeForBodilessEntityMono(requestValues).block();
        }
        ResponseEntity<Void> entity = responseEntity;
        Assert.state(entity != null, "Expected ResponseEntity");
        return entity;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> ResponseEntity<T> exchangeForEntity(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
        ResponseEntity<T> responseEntity;
        if (this.blockTimeout != null) {
            responseEntity = (ResponseEntity) exchangeForEntityMono(requestValues, bodyType).block(this.blockTimeout);
        } else {
            responseEntity = (ResponseEntity) exchangeForEntityMono(requestValues, bodyType).block();
        }
        ResponseEntity<T> entity = responseEntity;
        Assert.state(entity != null, "Expected ResponseEntity");
        return entity;
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public Mono<Void> requestToVoid(HttpRequestValues requestValues) {
        return exchangeForMono(requestValues);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public Mono<HttpHeaders> requestToHeaders(HttpRequestValues requestValues) {
        return exchangeForHeadersMono(requestValues);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public <T> Mono<T> requestToBody(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
        return exchangeForBodyMono(requestValues, bodyType);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public <T> Flux<T> requestToBodyFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
        return exchangeForBodyFlux(requestValues, bodyType);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public Mono<ResponseEntity<Void>> requestToBodilessEntity(HttpRequestValues requestValues) {
        return exchangeForBodilessEntityMono(requestValues);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public <T> Mono<ResponseEntity<T>> requestToEntity(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
        return exchangeForEntityMono(requestValues, bodyType);
    }

    @Override // org.springframework.web.service.invoker.HttpClientAdapter
    public <T> Mono<ResponseEntity<Flux<T>>> requestToEntityFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType) {
        return exchangeForEntityFlux(requestValues, bodyType);
    }
}
