package org.springframework.boot.web.server;

import java.security.KeyStore;

@Deprecated(since = "3.1.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/SslStoreProvider.class */
public interface SslStoreProvider {
    KeyStore getKeyStore() throws Exception;

    KeyStore getTrustStore() throws Exception;

    default String getKeyPassword() {
        return null;
    }
}
