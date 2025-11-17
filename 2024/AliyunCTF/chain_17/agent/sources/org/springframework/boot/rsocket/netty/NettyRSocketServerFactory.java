package org.springframework.boot.rsocket.netty;

import io.rsocket.SocketAcceptor;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.embedded.netty.SslServerCustomizer;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerSslBundle;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.AbstractProtocolSslContextSpec;
import reactor.netty.tcp.TcpServer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/netty/NettyRSocketServerFactory.class */
public class NettyRSocketServerFactory implements RSocketServerFactory, ConfigurableRSocketServerFactory {
    private DataSize fragmentSize;
    private InetAddress address;
    private ReactorResourceFactory resourceFactory;
    private Duration lifecycleTimeout;
    private Ssl ssl;
    private SslStoreProvider sslStoreProvider;
    private SslBundles sslBundles;
    private int port = 9898;
    private RSocketServer.Transport transport = RSocketServer.Transport.TCP;
    private List<RSocketServerCustomizer> rSocketServerCustomizers = new ArrayList();

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setFragmentSize(DataSize fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setTransport(RSocketServer.Transport transport) {
        this.transport = transport;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setSslStoreProvider(SslStoreProvider sslStoreProvider) {
        this.sslStoreProvider = sslStoreProvider;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setSslBundles(SslBundles sslBundles) {
        this.sslBundles = sslBundles;
    }

    public void setResourceFactory(ReactorResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public void setRSocketServerCustomizers(Collection<? extends RSocketServerCustomizer> rSocketServerCustomizers) {
        Assert.notNull(rSocketServerCustomizers, "RSocketServerCustomizers must not be null");
        this.rSocketServerCustomizers = new ArrayList(rSocketServerCustomizers);
    }

    public void addRSocketServerCustomizers(RSocketServerCustomizer... rSocketServerCustomizers) {
        Assert.notNull(rSocketServerCustomizers, "RSocketServerCustomizers must not be null");
        this.rSocketServerCustomizers.addAll(Arrays.asList(rSocketServerCustomizers));
    }

    public void setLifecycleTimeout(Duration lifecycleTimeout) {
        this.lifecycleTimeout = lifecycleTimeout;
    }

    @Override // org.springframework.boot.rsocket.server.RSocketServerFactory
    public NettyRSocketServer create(SocketAcceptor socketAcceptor) {
        ServerTransport<CloseableChannel> transport = createTransport();
        io.rsocket.core.RSocketServer server = io.rsocket.core.RSocketServer.create(socketAcceptor);
        configureServer(server);
        Mono<CloseableChannel> starter = server.bind(transport);
        return new NettyRSocketServer(starter, this.lifecycleTimeout);
    }

    private void configureServer(io.rsocket.core.RSocketServer server) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) this.fragmentSize).asInt((v0) -> {
            return v0.toBytes();
        });
        Objects.requireNonNull(server);
        asInt.to((v1) -> {
            r1.fragment(v1);
        });
        this.rSocketServerCustomizers.forEach(customizer -> {
            customizer.customize(server);
        });
    }

    private ServerTransport<CloseableChannel> createTransport() {
        if (this.transport == RSocketServer.Transport.WEBSOCKET) {
            return createWebSocketTransport();
        }
        return createTcpTransport();
    }

    private ServerTransport<CloseableChannel> createWebSocketTransport() {
        HttpServer httpServer = HttpServer.create();
        if (this.resourceFactory != null) {
            httpServer = (HttpServer) httpServer.runOn(this.resourceFactory.getLoopResources());
        }
        if (Ssl.isEnabled(this.ssl)) {
            httpServer = customizeSslConfiguration(httpServer);
        }
        return WebsocketServerTransport.create(httpServer.bindAddress(this::getListenAddress));
    }

    private HttpServer customizeSslConfiguration(HttpServer httpServer) {
        return new SslServerCustomizer(null, this.ssl.getClientAuth(), getSslBundle()).apply(httpServer);
    }

    private ServerTransport<CloseableChannel> createTcpTransport() {
        TcpServer tcpServer = TcpServer.create();
        if (this.resourceFactory != null) {
            tcpServer = tcpServer.runOn(this.resourceFactory.getLoopResources());
        }
        if (Ssl.isEnabled(this.ssl)) {
            tcpServer = new TcpSslServerCustomizer(this.ssl.getClientAuth(), getSslBundle()).apply(tcpServer);
        }
        return TcpServerTransport.create(tcpServer.bindAddress(this::getListenAddress));
    }

    private SslBundle getSslBundle() {
        return WebServerSslBundle.get(this.ssl, this.sslBundles, this.sslStoreProvider);
    }

    private InetSocketAddress getListenAddress() {
        if (this.address != null) {
            return new InetSocketAddress(this.address.getHostAddress(), this.port);
        }
        return new InetSocketAddress(this.port);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/netty/NettyRSocketServerFactory$TcpSslServerCustomizer.class */
    public static final class TcpSslServerCustomizer extends SslServerCustomizer {
        private final SslBundle sslBundle;

        private TcpSslServerCustomizer(Ssl.ClientAuth clientAuth, SslBundle sslBundle) {
            super(null, clientAuth, sslBundle);
            this.sslBundle = sslBundle;
        }

        private TcpServer apply(TcpServer server) {
            AbstractProtocolSslContextSpec<?> sslContextSpec = createSslContextSpec(this.sslBundle);
            return server.secure(spec -> {
                spec.sslContext(sslContextSpec);
            });
        }
    }
}
