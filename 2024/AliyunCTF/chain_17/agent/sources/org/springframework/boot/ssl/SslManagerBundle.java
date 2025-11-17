package org.springframework.boot.ssl;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslManagerBundle.class */
public interface SslManagerBundle {
    KeyManagerFactory getKeyManagerFactory();

    TrustManagerFactory getTrustManagerFactory();

    default KeyManager[] getKeyManagers() {
        return getKeyManagerFactory().getKeyManagers();
    }

    default TrustManager[] getTrustManagers() {
        return getTrustManagerFactory().getTrustManagers();
    }

    default SSLContext createSslContext(String protocol) {
        try {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(getKeyManagers(), getTrustManagers(), null);
            return sslContext;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load SSL context: " + ex.getMessage(), ex);
        }
    }

    static SslManagerBundle of(final KeyManagerFactory keyManagerFactory, final TrustManagerFactory trustManagerFactory) {
        Assert.notNull(keyManagerFactory, "KeyManagerFactory must not be null");
        Assert.notNull(trustManagerFactory, "TrustManagerFactory must not be null");
        return new SslManagerBundle() { // from class: org.springframework.boot.ssl.SslManagerBundle.1
            @Override // org.springframework.boot.ssl.SslManagerBundle
            public KeyManagerFactory getKeyManagerFactory() {
                return keyManagerFactory;
            }

            @Override // org.springframework.boot.ssl.SslManagerBundle
            public TrustManagerFactory getTrustManagerFactory() {
                return trustManagerFactory;
            }
        };
    }

    static SslManagerBundle from(SslStoreBundle storeBundle, SslBundleKey key) {
        return new DefaultSslManagerBundle(storeBundle, key);
    }
}
