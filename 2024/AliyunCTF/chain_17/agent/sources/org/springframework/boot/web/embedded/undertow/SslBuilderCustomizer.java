package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;
import java.net.InetAddress;
import javax.net.ssl.SSLContext;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.web.server.Ssl;
import org.xnio.Options;
import org.xnio.Sequence;
import org.xnio.SslClientAuthMode;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/SslBuilderCustomizer.class */
class SslBuilderCustomizer implements UndertowBuilderCustomizer {
    private final int port;
    private final InetAddress address;
    private final Ssl.ClientAuth clientAuth;
    private final SslBundle sslBundle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslBuilderCustomizer(int port, InetAddress address, Ssl.ClientAuth clientAuth, SslBundle sslBundle) {
        this.port = port;
        this.address = address;
        this.clientAuth = clientAuth;
        this.sslBundle = sslBundle;
    }

    @Override // org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
    public void customize(Undertow.Builder builder) {
        SslOptions options = this.sslBundle.getOptions();
        SSLContext sslContext = this.sslBundle.createSslContext();
        builder.addHttpsListener(this.port, getListenAddress(), sslContext);
        builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, (SslClientAuthMode) Ssl.ClientAuth.map(this.clientAuth, SslClientAuthMode.NOT_REQUESTED, SslClientAuthMode.REQUESTED, SslClientAuthMode.REQUIRED));
        if (options.getEnabledProtocols() != null) {
            builder.setSocketOption(Options.SSL_ENABLED_PROTOCOLS, Sequence.of(options.getEnabledProtocols()));
        }
        if (options.getCiphers() != null) {
            builder.setSocketOption(Options.SSL_ENABLED_CIPHER_SUITES, Sequence.of(options.getCiphers()));
        }
    }

    private String getListenAddress() {
        if (this.address == null) {
            return "0.0.0.0";
        }
        return this.address.getHostAddress();
    }
}
