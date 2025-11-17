package org.springframework.boot.ssl;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/DefaultSslManagerBundle.class */
public class DefaultSslManagerBundle implements SslManagerBundle {
    private final SslStoreBundle storeBundle;
    private final SslBundleKey key;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultSslManagerBundle(SslStoreBundle storeBundle, SslBundleKey key) {
        this.storeBundle = storeBundle != null ? storeBundle : SslStoreBundle.NONE;
        this.key = key != null ? key : SslBundleKey.NONE;
    }

    @Override // org.springframework.boot.ssl.SslManagerBundle
    public KeyManagerFactory getKeyManagerFactory() {
        try {
            KeyStore store = this.storeBundle.getKeyStore();
            this.key.assertContainsAlias(store);
            String alias = this.key.getAlias();
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory factory = getKeyManagerFactoryInstance(algorithm);
            KeyManagerFactory factory2 = alias != null ? new AliasKeyManagerFactory(factory, alias, algorithm) : factory;
            String password = this.key.getPassword();
            String password2 = password != null ? password : this.storeBundle.getKeyStorePassword();
            factory2.init(store, password2 != null ? password2.toCharArray() : null);
            return factory2;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new IllegalStateException("Could not load key manager factory: " + ex2.getMessage(), ex2);
        }
    }

    @Override // org.springframework.boot.ssl.SslManagerBundle
    public TrustManagerFactory getTrustManagerFactory() {
        try {
            KeyStore store = this.storeBundle.getTrustStore();
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory factory = getTrustManagerFactoryInstance(algorithm);
            factory.init(store);
            return factory;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load trust manager factory: " + ex.getMessage(), ex);
        }
    }

    protected KeyManagerFactory getKeyManagerFactoryInstance(String algorithm) throws NoSuchAlgorithmException {
        return KeyManagerFactory.getInstance(algorithm);
    }

    protected TrustManagerFactory getTrustManagerFactoryInstance(String algorithm) throws NoSuchAlgorithmException {
        return TrustManagerFactory.getInstance(algorithm);
    }
}
