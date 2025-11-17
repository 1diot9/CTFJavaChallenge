package org.springframework.boot.web.embedded.netty;

import io.netty.handler.ssl.ClientAuth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.AbstractProtocolSslContextSpec;
import reactor.netty.tcp.SslProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer.class */
public class SslServerCustomizer implements NettyServerCustomizer {
    private static final Log logger = LogFactory.getLog((Class<?>) SslServerCustomizer.class);
    private final Http2 http2;
    private final ClientAuth clientAuth;
    private volatile SslProvider sslProvider;
    private volatile SslBundle sslBundle;

    public SslServerCustomizer(Http2 http2, Ssl.ClientAuth clientAuth, SslBundle sslBundle) {
        this.http2 = http2;
        this.clientAuth = (ClientAuth) Ssl.ClientAuth.map(clientAuth, ClientAuth.NONE, ClientAuth.OPTIONAL, ClientAuth.REQUIRE);
        this.sslBundle = sslBundle;
        this.sslProvider = createSslProvider(sslBundle);
    }

    @Override // java.util.function.Function
    public HttpServer apply(HttpServer server) {
        return server.secure(this::applySecurity);
    }

    private void applySecurity(SslProvider.SslContextSpec spec) {
        spec.sslContext(this.sslProvider.getSslContext()).setSniAsyncMappings((domainName, promise) -> {
            return promise.setSuccess(this.sslProvider);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSslBundle(SslBundle sslBundle) {
        logger.debug("SSL Bundle has been updated, reloading SSL configuration");
        this.sslBundle = sslBundle;
        this.sslProvider = createSslProvider(sslBundle);
    }

    private SslProvider createSslProvider(SslBundle sslBundle) {
        return SslProvider.builder().sslContext(createSslContextSpec(sslBundle)).build();
    }

    @Deprecated(since = "3.2", forRemoval = true)
    protected AbstractProtocolSslContextSpec<?> createSslContextSpec() {
        return createSslContextSpec(this.sslBundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AbstractProtocolSslContextSpec<?> createSslContextSpec(SslBundle sslBundle) {
        Http2SslContextSpec forServer;
        if (this.http2 != null && this.http2.isEnabled()) {
            forServer = Http2SslContextSpec.forServer(sslBundle.getManagers().getKeyManagerFactory());
        } else {
            forServer = Http11SslContextSpec.forServer(sslBundle.getManagers().getKeyManagerFactory());
        }
        return forServer.configure(builder -> {
            builder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            SslOptions options = sslBundle.getOptions();
            builder.protocols(options.getEnabledProtocols());
            builder.ciphers(SslOptions.asSet(options.getCiphers()));
            builder.clientAuth(this.clientAuth);
        });
    }
}
