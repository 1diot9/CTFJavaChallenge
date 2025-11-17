package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.function.Consumer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/AutoConfiguredWebClientSsl.class */
class AutoConfiguredWebClientSsl implements WebClientSsl {
    private final ClientHttpConnectorFactory<?> clientHttpConnectorFactory;
    private final SslBundles sslBundles;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoConfiguredWebClientSsl(ClientHttpConnectorFactory<?> clientHttpConnectorFactory, SslBundles sslBundles) {
        this.clientHttpConnectorFactory = clientHttpConnectorFactory;
        this.sslBundles = sslBundles;
    }

    @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl
    public Consumer<WebClient.Builder> fromBundle(String bundleName) {
        return fromBundle(this.sslBundles.getBundle(bundleName));
    }

    @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl
    public Consumer<WebClient.Builder> fromBundle(SslBundle bundle) {
        return builder -> {
            builder.clientConnector((ClientHttpConnector) this.clientHttpConnectorFactory.createClientHttpConnector(bundle));
        };
    }
}
