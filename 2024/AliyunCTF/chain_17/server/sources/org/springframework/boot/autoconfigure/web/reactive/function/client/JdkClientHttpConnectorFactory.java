package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.net.http.HttpClient;
import javax.net.ssl.SSLParameters;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.http.client.reactive.JdkClientHttpConnector;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/JdkClientHttpConnectorFactory.class */
class JdkClientHttpConnectorFactory implements ClientHttpConnectorFactory<JdkClientHttpConnector> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorFactory
    public JdkClientHttpConnector createClientHttpConnector(SslBundle sslBundle) {
        HttpClient.Builder builder = HttpClient.newBuilder();
        if (sslBundle != null) {
            SslOptions options = sslBundle.getOptions();
            builder.sslContext(sslBundle.createSslContext());
            SSLParameters parameters = new SSLParameters();
            parameters.setCipherSuites(options.getCiphers());
            parameters.setProtocols(options.getEnabledProtocols());
            builder.sslParameters(parameters);
        }
        return new JdkClientHttpConnector(builder.build());
    }
}
