package org.springframework.web.service.invoker;

import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/ReactorHttpExchangeAdapter.class */
public interface ReactorHttpExchangeAdapter extends HttpExchangeAdapter {
    ReactiveAdapterRegistry getReactiveAdapterRegistry();

    @Nullable
    Duration getBlockTimeout();

    Mono<Void> exchangeForMono(HttpRequestValues requestValues);

    Mono<HttpHeaders> exchangeForHeadersMono(HttpRequestValues requestValues);

    <T> Mono<T> exchangeForBodyMono(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    <T> Flux<T> exchangeForBodyFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    Mono<ResponseEntity<Void>> exchangeForBodilessEntityMono(HttpRequestValues requestValues);

    <T> Mono<ResponseEntity<T>> exchangeForEntityMono(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    <T> Mono<ResponseEntity<Flux<T>>> exchangeForEntityFlux(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);
}
