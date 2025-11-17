package org.springframework.boot.web.embedded.netty;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/NettyReactiveWebServerFactory.class */
public class NettyReactiveWebServerFactory extends AbstractReactiveWebServerFactory {
    private Set<NettyServerCustomizer> serverCustomizers;
    private final List<NettyRouteProvider> routeProviders;
    private Duration lifecycleTimeout;
    private boolean useForwardHeaders;
    private ReactorResourceFactory resourceFactory;
    private Shutdown shutdown;

    public NettyReactiveWebServerFactory() {
        this.serverCustomizers = new LinkedHashSet();
        this.routeProviders = new ArrayList();
    }

    public NettyReactiveWebServerFactory(int port) {
        super(port);
        this.serverCustomizers = new LinkedHashSet();
        this.routeProviders = new ArrayList();
    }

    @Override // org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
    public WebServer getWebServer(HttpHandler httpHandler) {
        HttpServer httpServer = createHttpServer();
        ReactorHttpHandlerAdapter handlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        NettyWebServer webServer = createNettyWebServer(httpServer, handlerAdapter, this.lifecycleTimeout, getShutdown());
        webServer.setRouteProviders(this.routeProviders);
        return webServer;
    }

    NettyWebServer createNettyWebServer(HttpServer httpServer, ReactorHttpHandlerAdapter handlerAdapter, Duration lifecycleTimeout, Shutdown shutdown) {
        return new NettyWebServer(httpServer, handlerAdapter, lifecycleTimeout, shutdown, this.resourceFactory);
    }

    public Collection<NettyServerCustomizer> getServerCustomizers() {
        return this.serverCustomizers;
    }

    public void setServerCustomizers(Collection<? extends NettyServerCustomizer> serverCustomizers) {
        Assert.notNull(serverCustomizers, "ServerCustomizers must not be null");
        this.serverCustomizers = new LinkedHashSet(serverCustomizers);
    }

    public void addServerCustomizers(NettyServerCustomizer... serverCustomizers) {
        Assert.notNull(serverCustomizers, "ServerCustomizer must not be null");
        this.serverCustomizers.addAll(Arrays.asList(serverCustomizers));
    }

    public void addRouteProviders(NettyRouteProvider... routeProviders) {
        Assert.notNull(routeProviders, "NettyRouteProvider must not be null");
        this.routeProviders.addAll(Arrays.asList(routeProviders));
    }

    public void setLifecycleTimeout(Duration lifecycleTimeout) {
        this.lifecycleTimeout = lifecycleTimeout;
    }

    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    public void setResourceFactory(ReactorResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    @Override // org.springframework.boot.web.server.AbstractConfigurableWebServerFactory, org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setShutdown(Shutdown shutdown) {
        this.shutdown = shutdown;
    }

    @Override // org.springframework.boot.web.server.AbstractConfigurableWebServerFactory
    public Shutdown getShutdown() {
        return this.shutdown;
    }

    private HttpServer createHttpServer() {
        HttpServer server = HttpServer.create().bindAddress(this::getListenAddress);
        if (Ssl.isEnabled(getSsl())) {
            server = customizeSslConfiguration(server);
        }
        if (getCompression() != null && getCompression().getEnabled()) {
            CompressionCustomizer compressionCustomizer = new CompressionCustomizer(getCompression());
            server = compressionCustomizer.apply(server);
        }
        return applyCustomizers(server.protocol(listProtocols()).forwarded(this.useForwardHeaders));
    }

    private HttpServer customizeSslConfiguration(HttpServer httpServer) {
        SslServerCustomizer customizer = new SslServerCustomizer(getHttp2(), getSsl().getClientAuth(), getSslBundle());
        String bundleName = getSsl().getBundle();
        if (StringUtils.hasText(bundleName)) {
            SslBundles sslBundles = getSslBundles();
            Objects.requireNonNull(customizer);
            sslBundles.addBundleUpdateHandler(bundleName, customizer::updateSslBundle);
        }
        return customizer.apply(httpServer);
    }

    private HttpProtocol[] listProtocols() {
        List<HttpProtocol> protocols = new ArrayList<>();
        protocols.add(HttpProtocol.HTTP11);
        if (getHttp2() != null && getHttp2().isEnabled()) {
            if (getSsl() != null && getSsl().isEnabled()) {
                protocols.add(HttpProtocol.H2);
            } else {
                protocols.add(HttpProtocol.H2C);
            }
        }
        return (HttpProtocol[]) protocols.toArray(new HttpProtocol[0]);
    }

    private InetSocketAddress getListenAddress() {
        if (getAddress() != null) {
            return new InetSocketAddress(getAddress().getHostAddress(), getPort());
        }
        return new InetSocketAddress(getPort());
    }

    private HttpServer applyCustomizers(HttpServer server) {
        for (NettyServerCustomizer customizer : this.serverCustomizers) {
            server = customizer.apply(server);
        }
        return server;
    }
}
