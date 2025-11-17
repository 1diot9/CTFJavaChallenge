package org.springframework.boot.autoconfigure.web.reactive.function.client;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.http.client.reactive.ClientHttpConnector;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorFactory.class */
interface ClientHttpConnectorFactory<T extends ClientHttpConnector> {
    T createClientHttpConnector(SslBundle sslBundle);

    default T createClientHttpConnector() {
        return createClientHttpConnector(null);
    }
}
