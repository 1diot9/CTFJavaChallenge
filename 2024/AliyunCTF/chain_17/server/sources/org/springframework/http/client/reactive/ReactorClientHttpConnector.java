package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorClientHttpConnector.class */
public class ReactorClientHttpConnector implements ClientHttpConnector, SmartLifecycle {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorClientHttpConnector.class);
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> {
        return client.compress(true);
    };
    private HttpClient httpClient;

    @Nullable
    private final org.springframework.http.client.ReactorResourceFactory resourceFactory;

    @Nullable
    private final Function<HttpClient, HttpClient> mapper;
    private volatile boolean running;
    private final Object lifecycleMonitor;

    public ReactorClientHttpConnector() {
        this.running = true;
        this.lifecycleMonitor = new Object();
        this.httpClient = defaultInitializer.apply(HttpClient.create());
        this.resourceFactory = null;
        this.mapper = null;
    }

    public ReactorClientHttpConnector(org.springframework.http.client.ReactorResourceFactory resourceFactory, Function<HttpClient, HttpClient> mapper) {
        this.running = true;
        this.lifecycleMonitor = new Object();
        this.httpClient = createHttpClient(resourceFactory, mapper);
        this.resourceFactory = resourceFactory;
        this.mapper = mapper;
    }

    private static HttpClient createHttpClient(org.springframework.http.client.ReactorResourceFactory resourceFactory, Function<HttpClient, HttpClient> mapper) {
        ConnectionProvider provider = resourceFactory.getConnectionProvider();
        Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
        return (HttpClient) defaultInitializer.andThen(mapper).andThen(applyLoopResources(resourceFactory)).apply(HttpClient.create(provider));
    }

    private static Function<HttpClient, HttpClient> applyLoopResources(org.springframework.http.client.ReactorResourceFactory factory) {
        return httpClient -> {
            LoopResources resources = factory.getLoopResources();
            Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
            return httpClient.runOn(resources);
        };
    }

    public ReactorClientHttpConnector(HttpClient httpClient) {
        this.running = true;
        this.lifecycleMonitor = new Object();
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
        this.resourceFactory = null;
        this.mapper = null;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        AtomicReference<ReactorClientHttpResponse> responseRef = new AtomicReference<>();
        HttpClient.RequestSender requestSender = this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name()));
        return setUri(requestSender, uri).send((request, outbound) -> {
            return (Publisher) requestCallback.apply(adaptRequest(method, uri, request, outbound));
        }).responseConnection((response, connection) -> {
            responseRef.set(new ReactorClientHttpResponse(response, connection));
            return Mono.just((ClientHttpResponse) responseRef.get());
        }).next().doOnCancel(() -> {
            ReactorClientHttpResponse response2 = (ReactorClientHttpResponse) responseRef.get();
            if (response2 != null) {
                response2.releaseAfterCancel(method);
            }
        });
    }

    private static HttpClient.RequestSender setUri(HttpClient.RequestSender requestSender, URI uri) {
        if (uri.isAbsolute()) {
            try {
                return requestSender.uri(uri);
            } catch (Exception e) {
            }
        }
        return requestSender.uri(uri.toString());
    }

    private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
        return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        synchronized (this.lifecycleMonitor) {
            if (!isRunning()) {
                if (this.resourceFactory != null && this.mapper != null) {
                    this.httpClient = createHttpClient(this.resourceFactory, this.mapper);
                } else {
                    logger.warn("Restarting a ReactorClientHttpConnector bean is only supported with externally managed Reactor Netty resources");
                }
                this.running = true;
            }
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        synchronized (this.lifecycleMonitor) {
            if (isRunning()) {
                this.running = false;
            }
        }
    }

    @Override // org.springframework.context.SmartLifecycle
    public final void stop(Runnable callback) {
        synchronized (this.lifecycleMonitor) {
            stop();
            callback.run();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.running;
    }

    @Override // org.springframework.context.SmartLifecycle
    public boolean isAutoStartup() {
        return false;
    }

    @Override // org.springframework.context.SmartLifecycle, org.springframework.context.Phased
    public int getPhase() {
        return 1;
    }
}
