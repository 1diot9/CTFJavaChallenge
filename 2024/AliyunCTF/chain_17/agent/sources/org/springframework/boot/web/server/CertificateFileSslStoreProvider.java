package org.springframework.boot.web.server;

import java.security.KeyStore;
import org.springframework.boot.ssl.SslBundle;

@Deprecated(since = "3.1.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/CertificateFileSslStoreProvider.class */
public final class CertificateFileSslStoreProvider implements SslStoreProvider {
    private final SslBundle delegate;

    private CertificateFileSslStoreProvider(SslBundle delegate) {
        this.delegate = delegate;
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public KeyStore getKeyStore() throws Exception {
        return this.delegate.getStores().getKeyStore();
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public KeyStore getTrustStore() throws Exception {
        return this.delegate.getStores().getTrustStore();
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public String getKeyPassword() {
        return this.delegate.getKey().getPassword();
    }

    public static SslStoreProvider from(Ssl ssl) {
        SslBundle delegate = WebServerSslBundle.createCertificateFileSslStoreProviderDelegate(ssl);
        if (delegate != null) {
            return new CertificateFileSslStoreProvider(delegate);
        }
        return null;
    }
}
