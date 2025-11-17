package org.springframework.boot.web.server;

import java.security.KeyStore;
import org.springframework.boot.ssl.SslBundleKey;

@Deprecated(since = "3.1.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/SslConfigurationValidator.class */
public final class SslConfigurationValidator {
    private SslConfigurationValidator() {
    }

    public static void validateKeyAlias(KeyStore keyStore, String keyAlias) {
        SslBundleKey.of(null, keyAlias).assertContainsAlias(keyStore);
    }
}
