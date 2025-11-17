package org.springframework.boot.autoconfigure.web.client;

import java.util.function.Consumer;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.web.client.RestClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/RestClientSsl.class */
public interface RestClientSsl {
    Consumer<RestClient.Builder> fromBundle(String bundleName) throws NoSuchSslBundleException;

    Consumer<RestClient.Builder> fromBundle(SslBundle bundle);
}
