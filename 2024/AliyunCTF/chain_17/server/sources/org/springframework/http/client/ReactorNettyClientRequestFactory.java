package org.springframework.http.client;

import io.netty.channel.ChannelOption;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ReactorNettyClientRequestFactory.class */
public class ReactorNettyClientRequestFactory implements ClientHttpRequestFactory, SmartLifecycle {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorNettyClientRequestFactory.class);
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> {
        return client.compress(true);
    };
    private HttpClient httpClient;

    @Nullable
    private final ReactorResourceFactory resourceFactory;

    @Nullable
    private final Function<HttpClient, HttpClient> mapper;
    private Duration exchangeTimeout;
    private Duration readTimeout;
    private volatile boolean running;
    private final Object lifecycleMonitor;

    public ReactorNettyClientRequestFactory() {
        this.exchangeTimeout = Duration.ofSeconds(5L);
        this.readTimeout = Duration.ofSeconds(10L);
        this.running = true;
        this.lifecycleMonitor = new Object();
        this.httpClient = defaultInitializer.apply(HttpClient.create());
        this.resourceFactory = null;
        this.mapper = null;
    }

    public ReactorNettyClientRequestFactory(HttpClient httpClient) {
        this.exchangeTimeout = Duration.ofSeconds(5L);
        this.readTimeout = Duration.ofSeconds(10L);
        this.running = true;
        this.lifecycleMonitor = new Object();
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
        this.resourceFactory = null;
        this.mapper = null;
    }

    public ReactorNettyClientRequestFactory(ReactorResourceFactory resourceFactory, Function<HttpClient, HttpClient> mapper) {
        this.exchangeTimeout = Duration.ofSeconds(5L);
        this.readTimeout = Duration.ofSeconds(10L);
        this.running = true;
        this.lifecycleMonitor = new Object();
        this.httpClient = createHttpClient(resourceFactory, mapper);
        this.resourceFactory = resourceFactory;
        this.mapper = mapper;
    }

    private static HttpClient createHttpClient(ReactorResourceFactory resourceFactory, Function<HttpClient, HttpClient> mapper) {
        ConnectionProvider provider = resourceFactory.getConnectionProvider();
        Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
        return (HttpClient) defaultInitializer.andThen(mapper).andThen(applyLoopResources(resourceFactory)).apply(HttpClient.create(provider));
    }

    private static Function<HttpClient, HttpClient> applyLoopResources(ReactorResourceFactory factory) {
        return httpClient -> {
            LoopResources resources = factory.getLoopResources();
            Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
            return httpClient.runOn(resources);
        };
    }

    public void setConnectTimeout(int connectTimeout) {
        Assert.isTrue(connectTimeout >= 0, "Timeout must be a non-negative value");
        this.httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(connectTimeout));
    }

    public void setConnectTimeout(Duration connectTimeout) {
        Assert.notNull(connectTimeout, "ConnectTimeout must not be null");
        Assert.isTrue(!connectTimeout.isNegative(), "Timeout must be a non-negative value");
        this.httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf((int) connectTimeout.toMillis()));
    }

    public void setReadTimeout(long readTimeout) {
        Assert.isTrue(readTimeout > 0, "Timeout must be a positive value");
        this.readTimeout = Duration.ofMillis(readTimeout);
    }

    public void setReadTimeout(Duration readTimeout) {
        Assert.notNull(readTimeout, "ReadTimeout must not be null");
        Assert.isTrue(!readTimeout.isNegative(), "Timeout must be a non-negative value");
        this.readTimeout = readTimeout;
    }

    public void setExchangeTimeout(long exchangeTimeout) {
        Assert.isTrue(exchangeTimeout > 0, "Timeout must be a positive value");
        this.exchangeTimeout = Duration.ofMillis(exchangeTimeout);
    }

    public void setExchangeTimeout(Duration exchangeTimeout) {
        Assert.notNull(exchangeTimeout, "ExchangeTimeout must not be null");
        Assert.isTrue(!exchangeTimeout.isNegative(), "Timeout must be a non-negative value");
        this.exchangeTimeout = exchangeTimeout;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return new ReactorNettyClientRequest(this.httpClient, uri, httpMethod, this.exchangeTimeout, this.readTimeout);
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        synchronized (this.lifecycleMonitor) {
            if (!isRunning()) {
                if (this.resourceFactory != null && this.mapper != null) {
                    this.httpClient = createHttpClient(this.resourceFactory, this.mapper);
                } else {
                    logger.warn("Restarting a ReactorNettyClientRequestFactory bean is only supported with externally managed Reactor Netty resources");
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
