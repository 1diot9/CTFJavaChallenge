package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty5.NettyOutbound;
import reactor.netty5.http.client.HttpClient;
import reactor.netty5.http.client.HttpClientRequest;
import reactor.netty5.resources.ConnectionProvider;
import reactor.netty5.resources.LoopResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorNetty2ClientHttpConnector.class */
public class ReactorNetty2ClientHttpConnector implements ClientHttpConnector {
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> {
        return client.compress(true);
    };
    private final HttpClient httpClient;

    public ReactorNetty2ClientHttpConnector() {
        this.httpClient = defaultInitializer.apply(HttpClient.create().wiretap(true));
    }

    public ReactorNetty2ClientHttpConnector(ReactorNetty2ResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
        ConnectionProvider provider = factory.getConnectionProvider();
        Assert.notNull(provider, "No ConnectionProvider: is ReactorNetty2ResourceFactory not initialized yet?");
        this.httpClient = (HttpClient) defaultInitializer.andThen(mapper).andThen(applyLoopResources(factory)).apply(HttpClient.create(provider));
    }

    private static Function<HttpClient, HttpClient> applyLoopResources(ReactorNetty2ResourceFactory factory) {
        return httpClient -> {
            LoopResources resources = factory.getLoopResources();
            Assert.notNull(resources, "No LoopResources: is ReactorNetty2ResourceFactory not initialized yet?");
            return httpClient.runOn(resources);
        };
    }

    public ReactorNetty2ClientHttpConnector(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        AtomicReference<ReactorNetty2ClientHttpResponse> responseRef = new AtomicReference<>();
        HttpClient.RequestSender requestSender = this.httpClient.request(io.netty5.handler.codec.http.HttpMethod.valueOf(method.name()));
        return (uri.isAbsolute() ? (HttpClient.RequestSender) requestSender.uri(uri) : requestSender.uri(uri.toString())).send((request, outbound) -> {
            return (Publisher) requestCallback.apply(adaptRequest(method, uri, request, outbound));
        }).responseConnection((response, connection) -> {
            responseRef.set(new ReactorNetty2ClientHttpResponse(response, connection));
            return Mono.just((ClientHttpResponse) responseRef.get());
        }).next().doOnCancel(() -> {
            ReactorNetty2ClientHttpResponse response2 = (ReactorNetty2ClientHttpResponse) responseRef.get();
            if (response2 != null) {
                response2.releaseAfterCancel(method);
            }
        });
    }

    private ReactorNetty2ClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
        return new ReactorNetty2ClientHttpRequest(method, uri, request, nettyOutbound);
    }
}
