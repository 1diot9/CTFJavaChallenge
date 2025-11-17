package org.springframework.http.client.reactive;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStreamResetException;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/HttpComponentsClientHttpConnector.class */
public class HttpComponentsClientHttpConnector implements ClientHttpConnector, Closeable {
    private final CloseableHttpAsyncClient client;
    private final BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider;
    private DataBufferFactory dataBufferFactory;

    public HttpComponentsClientHttpConnector() {
        this(HttpAsyncClients.createDefault());
    }

    public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client) {
        this(client, (method, uri) -> {
            return HttpClientContext.create();
        });
    }

    public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client, BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider) {
        this.dataBufferFactory = DefaultDataBufferFactory.sharedInstance;
        Assert.notNull(client, "Client must not be null");
        Assert.notNull(contextProvider, "ContextProvider must not be null");
        this.contextProvider = contextProvider;
        this.client = client;
        this.client.start();
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        this.dataBufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        HttpClientContext context = this.contextProvider.apply(method, uri);
        if (context.getCookieStore() == null) {
            context.setCookieStore(new BasicCookieStore());
        }
        HttpComponentsClientHttpRequest request = new HttpComponentsClientHttpRequest(method, uri, context, this.dataBufferFactory);
        return requestCallback.apply(request).then(Mono.defer(() -> {
            return execute(request, context);
        }));
    }

    private Mono<ClientHttpResponse> execute(HttpComponentsClientHttpRequest request, HttpClientContext context) {
        AsyncRequestProducer requestProducer = request.toRequestProducer();
        return Mono.create(sink -> {
            ReactiveResponseConsumer reactiveResponseConsumer = new ReactiveResponseConsumer(new ResponseCallback(sink, this.dataBufferFactory, context));
            this.client.execute(requestProducer, reactiveResponseConsumer, context, new ResultCallback(sink));
        });
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.client.close();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/HttpComponentsClientHttpConnector$ResponseCallback.class */
    private static class ResponseCallback implements FutureCallback<Message<HttpResponse, Publisher<ByteBuffer>>> {
        private final MonoSink<ClientHttpResponse> sink;
        private final DataBufferFactory dataBufferFactory;
        private final HttpClientContext context;

        public ResponseCallback(MonoSink<ClientHttpResponse> sink, DataBufferFactory dataBufferFactory, HttpClientContext context) {
            this.sink = sink;
            this.dataBufferFactory = dataBufferFactory;
            this.context = context;
        }

        public void completed(Message<HttpResponse, Publisher<ByteBuffer>> result) {
            this.sink.success(new HttpComponentsClientHttpResponse(this.dataBufferFactory, result, this.context));
        }

        public void failed(Exception ex) {
            this.sink.error((!(ex instanceof HttpStreamResetException) || ex.getCause() == null) ? ex : ex.getCause());
        }

        public void cancelled() {
            this.sink.error(new CancellationException());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/HttpComponentsClientHttpConnector$ResultCallback.class */
    private static class ResultCallback implements FutureCallback<Void> {
        private final MonoSink<?> sink;

        public ResultCallback(MonoSink<?> sink) {
            this.sink = sink;
        }

        public void completed(Void result) {
            this.sink.success();
        }

        public void failed(Exception ex) {
            this.sink.error((!(ex instanceof HttpStreamResetException) || ex.getCause() == null) ? ex : ex.getCause());
        }

        public void cancelled() {
            this.sink.error(new CancellationException());
        }
    }
}
