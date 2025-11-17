package org.springframework.boot.web.embedded.jetty;

import java.net.InetSocketAddress;
import java.util.function.Supplier;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/SslServerCustomizer.class */
public class SslServerCustomizer implements JettyServerCustomizer {
    private final Http2 http2;
    private final InetSocketAddress address;
    private final Ssl.ClientAuth clientAuth;
    private final SslBundle sslBundle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslServerCustomizer(Http2 http2, InetSocketAddress address, Ssl.ClientAuth clientAuth, SslBundle sslBundle) {
        this.address = address;
        this.clientAuth = clientAuth;
        this.sslBundle = sslBundle;
        this.http2 = http2;
    }

    @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
    public void customize(Server server) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setEndpointIdentificationAlgorithm((String) null);
        configureSsl(sslContextFactory, this.clientAuth);
        server.setConnectors(new Connector[]{createConnector(server, sslContextFactory)});
    }

    private ServerConnector createConnector(Server server, SslContextFactory.Server sslContextFactory) {
        HttpConfiguration config = new HttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.setSecurePort(this.address.getPort());
        config.addCustomizer(new SecureRequestCustomizer());
        ServerConnector connector = createServerConnector(server, sslContextFactory, config);
        connector.setPort(this.address.getPort());
        connector.setHost(this.address.getHostString());
        return connector;
    }

    private ServerConnector createServerConnector(Server server, SslContextFactory.Server sslContextFactory, HttpConfiguration config) {
        if (this.http2 == null || !this.http2.isEnabled()) {
            return createHttp11ServerConnector(config, sslContextFactory, server);
        }
        Assert.state(isJettyAlpnPresent(), (Supplier<String>) () -> {
            return "An 'org.eclipse.jetty:jetty-alpn-*-server' dependency is required for HTTP/2 support.";
        });
        Assert.state(isJettyHttp2Present(), (Supplier<String>) () -> {
            return "The 'org.eclipse.jetty.http2:jetty-http2-server' dependency is required for HTTP/2 support.";
        });
        return createHttp2ServerConnector(config, sslContextFactory, server);
    }

    private ServerConnector createHttp11ServerConnector(HttpConfiguration config, SslContextFactory.Server sslContextFactory, Server server) {
        SslConnectionFactory sslConnectionFactory = createSslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
        HttpConnectionFactory connectionFactory = new HttpConnectionFactory(config);
        return new SslValidatingServerConnector(this.sslBundle.getKey(), sslContextFactory, server, sslConnectionFactory, connectionFactory);
    }

    private SslConnectionFactory createSslConnectionFactory(SslContextFactory.Server sslContextFactory, String protocol) {
        return new SslConnectionFactory(sslContextFactory, protocol);
    }

    private boolean isJettyAlpnPresent() {
        return ClassUtils.isPresent("org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory", null);
    }

    private boolean isJettyHttp2Present() {
        return ClassUtils.isPresent("org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory", null);
    }

    private ServerConnector createHttp2ServerConnector(HttpConfiguration config, SslContextFactory.Server sslContextFactory, Server server) {
        ConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);
        ConnectionFactory hTTP2ServerConnectionFactory = new HTTP2ServerConnectionFactory(config);
        ConnectionFactory createAlpnServerConnectionFactory = createAlpnServerConnectionFactory();
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        if (isConscryptPresent()) {
            sslContextFactory.setProvider("Conscrypt");
        }
        return new SslValidatingServerConnector(this.sslBundle.getKey(), sslContextFactory, server, createSslConnectionFactory(sslContextFactory, createAlpnServerConnectionFactory.getProtocol()), createAlpnServerConnectionFactory, hTTP2ServerConnectionFactory, httpConnectionFactory);
    }

    private ALPNServerConnectionFactory createAlpnServerConnectionFactory() {
        try {
            return new ALPNServerConnectionFactory(new String[0]);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("An 'org.eclipse.jetty:jetty-alpn-*-server' dependency is required for HTTP/2 support.", ex);
        }
    }

    private boolean isConscryptPresent() {
        return ClassUtils.isPresent("org.conscrypt.Conscrypt", null) && ClassUtils.isPresent("org.eclipse.jetty.alpn.conscrypt.server.ConscryptServerALPNProcessor", null);
    }

    protected void configureSsl(SslContextFactory.Server factory, Ssl.ClientAuth clientAuth) {
        SslBundleKey key = this.sslBundle.getKey();
        SslOptions options = this.sslBundle.getOptions();
        SslStoreBundle stores = this.sslBundle.getStores();
        factory.setProtocol(this.sslBundle.getProtocol());
        configureSslClientAuth(factory, clientAuth);
        if (stores.getKeyStorePassword() != null) {
            factory.setKeyStorePassword(stores.getKeyStorePassword());
        }
        factory.setCertAlias(key.getAlias());
        if (options.getCiphers() != null) {
            factory.setIncludeCipherSuites(options.getCiphers());
            factory.setExcludeCipherSuites(new String[0]);
        }
        if (options.getEnabledProtocols() != null) {
            factory.setIncludeProtocols(options.getEnabledProtocols());
            factory.setExcludeProtocols(new String[0]);
        }
        try {
            if (key.getPassword() != null) {
                factory.setKeyManagerPassword(key.getPassword());
            }
            factory.setKeyStore(stores.getKeyStore());
            factory.setTrustStore(stores.getTrustStore());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to set SSL store: " + ex.getMessage(), ex);
        }
    }

    private void configureSslClientAuth(SslContextFactory.Server factory, Ssl.ClientAuth clientAuth) {
        factory.setWantClientAuth(clientAuth == Ssl.ClientAuth.WANT || clientAuth == Ssl.ClientAuth.NEED);
        factory.setNeedClientAuth(clientAuth == Ssl.ClientAuth.NEED);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/SslServerCustomizer$SslValidatingServerConnector.class */
    public static class SslValidatingServerConnector extends ServerConnector {
        private final SslBundleKey key;
        private final SslContextFactory sslContextFactory;

        /* JADX WARN: Multi-variable type inference failed */
        SslValidatingServerConnector(SslBundleKey key, SslContextFactory sslContextFactory, Server server, SslConnectionFactory sslConnectionFactory, HttpConnectionFactory connectionFactory) {
            super(server, new ConnectionFactory[]{sslConnectionFactory, connectionFactory});
            this.key = key;
            this.sslContextFactory = sslContextFactory;
        }

        SslValidatingServerConnector(SslBundleKey keyAlias, SslContextFactory sslContextFactory, Server server, ConnectionFactory... factories) {
            super(server, factories);
            this.key = keyAlias;
            this.sslContextFactory = sslContextFactory;
        }

        protected void doStart() throws Exception {
            super.doStart();
            this.key.assertContainsAlias(this.sslContextFactory.getKeyStore());
        }
    }
}
