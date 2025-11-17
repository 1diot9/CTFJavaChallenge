package org.springframework.boot.autoconfigure.web.client;

import java.util.function.Consumer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/AutoConfiguredRestClientSsl.class */
class AutoConfiguredRestClientSsl implements RestClientSsl {
    private final SslBundles sslBundles;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoConfiguredRestClientSsl(SslBundles sslBundles) {
        this.sslBundles = sslBundles;
    }

    @Override // org.springframework.boot.autoconfigure.web.client.RestClientSsl
    public Consumer<RestClient.Builder> fromBundle(String bundleName) {
        return fromBundle(this.sslBundles.getBundle(bundleName));
    }

    @Override // org.springframework.boot.autoconfigure.web.client.RestClientSsl
    public Consumer<RestClient.Builder> fromBundle(SslBundle bundle) {
        return builder -> {
            ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS.withSslBundle(bundle);
            ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
            builder.requestFactory(requestFactory);
        };
    }
}
