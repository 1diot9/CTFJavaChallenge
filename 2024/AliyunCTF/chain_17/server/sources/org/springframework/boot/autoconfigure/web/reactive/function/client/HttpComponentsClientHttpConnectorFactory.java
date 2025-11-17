package org.springframework.boot.autoconfigure.web.reactive.function.client;

import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.nio.ssl.BasicClientTlsStrategy;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/HttpComponentsClientHttpConnectorFactory.class */
class HttpComponentsClientHttpConnectorFactory implements ClientHttpConnectorFactory<HttpComponentsClientHttpConnector> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorFactory
    public HttpComponentsClientHttpConnector createClientHttpConnector(SslBundle sslBundle) {
        HttpAsyncClientBuilder builder = HttpAsyncClients.custom().useSystemProperties();
        if (sslBundle != null) {
            SslOptions options = sslBundle.getOptions();
            SSLContext sslContext = sslBundle.createSslContext();
            SSLSessionVerifier sessionVerifier = (endpoint, sslEngine) -> {
                if (options.getCiphers() != null) {
                    sslEngine.setEnabledCipherSuites(options.getCiphers());
                }
                if (options.getEnabledProtocols() != null) {
                    sslEngine.setEnabledProtocols(options.getEnabledProtocols());
                    return null;
                }
                return null;
            };
            BasicClientTlsStrategy tlsStrategy = new BasicClientTlsStrategy(sslContext, sessionVerifier);
            builder.setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create().setTlsStrategy(tlsStrategy).build());
        }
        return new HttpComponentsClientHttpConnector(builder.build());
    }
}
