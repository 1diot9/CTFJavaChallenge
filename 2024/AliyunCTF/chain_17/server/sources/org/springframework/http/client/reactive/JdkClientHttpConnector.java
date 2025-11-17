package org.springframework.http.client.reactive;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.function.Function;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JdkClientHttpConnector.class */
public class JdkClientHttpConnector implements ClientHttpConnector {
    private final HttpClient httpClient;
    private DataBufferFactory bufferFactory;

    public JdkClientHttpConnector() {
        this(HttpClient.newHttpClient());
    }

    public JdkClientHttpConnector(HttpClient httpClient) {
        this.bufferFactory = DefaultDataBufferFactory.sharedInstance;
        this.httpClient = httpClient;
    }

    public JdkClientHttpConnector(HttpClient.Builder clientBuilder, @Nullable JdkHttpClientResourceFactory resourceFactory) {
        this.bufferFactory = DefaultDataBufferFactory.sharedInstance;
        if (resourceFactory != null) {
            Executor executor = resourceFactory.getExecutor();
            clientBuilder.executor(executor);
        }
        this.httpClient = clientBuilder.build();
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        Assert.notNull(bufferFactory, "DataBufferFactory is required");
        this.bufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        JdkClientHttpRequest jdkClientHttpRequest = new JdkClientHttpRequest(method, uri, this.bufferFactory);
        return requestCallback.apply(jdkClientHttpRequest).then(Mono.defer(() -> {
            HttpRequest httpRequest = (HttpRequest) jdkClientHttpRequest.getNativeRequest();
            CompletableFuture<HttpResponse<Flow.Publisher<List<ByteBuffer>>>> future = this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofPublisher());
            return Mono.fromCompletionStage(future).map(response -> {
                return new JdkClientHttpResponse(response, this.bufferFactory);
            });
        }));
    }
}
