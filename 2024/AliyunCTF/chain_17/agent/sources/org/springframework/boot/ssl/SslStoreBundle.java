package org.springframework.boot.ssl;

import java.security.KeyStore;
import org.springframework.core.style.ToStringCreator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslStoreBundle.class */
public interface SslStoreBundle {
    public static final SslStoreBundle NONE = of(null, null, null);

    KeyStore getKeyStore();

    String getKeyStorePassword();

    KeyStore getTrustStore();

    static SslStoreBundle of(final KeyStore keyStore, final String keyStorePassword, final KeyStore trustStore) {
        return new SslStoreBundle() { // from class: org.springframework.boot.ssl.SslStoreBundle.1
            @Override // org.springframework.boot.ssl.SslStoreBundle
            public KeyStore getKeyStore() {
                return keyStore;
            }

            @Override // org.springframework.boot.ssl.SslStoreBundle
            public KeyStore getTrustStore() {
                return trustStore;
            }

            @Override // org.springframework.boot.ssl.SslStoreBundle
            public String getKeyStorePassword() {
                return keyStorePassword;
            }

            public String toString() {
                ToStringCreator creator = new ToStringCreator(this);
                creator.append("keyStore.type", keyStore != null ? keyStore.getType() : "none");
                creator.append("keyStorePassword", keyStorePassword != null ? "******" : null);
                creator.append("trustStore.type", trustStore != null ? trustStore.getType() : "none");
                return creator.toString();
            }
        };
    }
}
