package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.function.Consumer;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.web.reactive.function.client.WebClient;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientSsl.class */
public interface WebClientSsl {
    Consumer<WebClient.Builder> fromBundle(String bundleName) throws NoSuchSslBundleException;

    Consumer<WebClient.Builder> fromBundle(SslBundle bundle);
}
