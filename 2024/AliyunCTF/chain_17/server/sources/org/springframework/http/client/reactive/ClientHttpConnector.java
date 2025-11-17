package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ClientHttpConnector.class */
public interface ClientHttpConnector {
    Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback);
}
