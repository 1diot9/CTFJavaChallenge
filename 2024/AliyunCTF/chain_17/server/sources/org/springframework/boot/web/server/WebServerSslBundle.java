package org.springframework.boot.web.server;

import java.security.KeyStore;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslManagerBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.jks.JksSslStoreBundle;
import org.springframework.boot.ssl.jks.JksSslStoreDetails;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/WebServerSslBundle.class */
public final class WebServerSslBundle implements SslBundle {
    private final SslStoreBundle stores;
    private final SslBundleKey key;
    private final SslOptions options;
    private final String protocol;
    private final SslManagerBundle managers;

    private WebServerSslBundle(SslStoreBundle stores, String keyPassword, Ssl ssl) {
        this.stores = stores;
        this.key = SslBundleKey.of(keyPassword, ssl.getKeyAlias());
        this.protocol = ssl.getProtocol();
        this.options = SslOptions.of(ssl.getCiphers(), ssl.getEnabledProtocols());
        this.managers = SslManagerBundle.from(this.stores, this.key);
    }

    private static SslStoreBundle createPemStoreBundle(Ssl ssl) {
        PemSslStoreDetails keyStoreDetails = new PemSslStoreDetails(ssl.getKeyStoreType(), ssl.getCertificate(), ssl.getCertificatePrivateKey()).withAlias(ssl.getKeyAlias());
        PemSslStoreDetails trustStoreDetails = new PemSslStoreDetails(ssl.getTrustStoreType(), ssl.getTrustCertificate(), ssl.getTrustCertificatePrivateKey());
        return new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
    }

    private static SslStoreBundle createPemKeyStoreBundle(Ssl ssl) {
        PemSslStoreDetails keyStoreDetails = new PemSslStoreDetails(ssl.getKeyStoreType(), ssl.getCertificate(), ssl.getCertificatePrivateKey()).withAlias(ssl.getKeyAlias());
        return new PemSslStoreBundle(keyStoreDetails, (PemSslStoreDetails) null);
    }

    private static SslStoreBundle createPemTrustStoreBundle(Ssl ssl) {
        PemSslStoreDetails trustStoreDetails = new PemSslStoreDetails(ssl.getTrustStoreType(), ssl.getTrustCertificate(), ssl.getTrustCertificatePrivateKey()).withAlias(ssl.getKeyAlias());
        return new PemSslStoreBundle((PemSslStoreDetails) null, trustStoreDetails);
    }

    private static SslStoreBundle createJksKeyStoreBundle(Ssl ssl) {
        JksSslStoreDetails keyStoreDetails = new JksSslStoreDetails(ssl.getKeyStoreType(), ssl.getKeyStoreProvider(), ssl.getKeyStore(), ssl.getKeyStorePassword());
        return new JksSslStoreBundle(keyStoreDetails, null);
    }

    private static SslStoreBundle createJksTrustStoreBundle(Ssl ssl) {
        JksSslStoreDetails trustStoreDetails = new JksSslStoreDetails(ssl.getTrustStoreType(), ssl.getTrustStoreProvider(), ssl.getTrustStore(), ssl.getTrustStorePassword());
        return new JksSslStoreBundle(null, trustStoreDetails);
    }

    @Override // org.springframework.boot.ssl.SslBundle
    public SslStoreBundle getStores() {
        return this.stores;
    }

    @Override // org.springframework.boot.ssl.SslBundle
    public SslBundleKey getKey() {
        return this.key;
    }

    @Override // org.springframework.boot.ssl.SslBundle
    public SslOptions getOptions() {
        return this.options;
    }

    @Override // org.springframework.boot.ssl.SslBundle
    public String getProtocol() {
        return this.protocol;
    }

    @Override // org.springframework.boot.ssl.SslBundle
    public SslManagerBundle getManagers() {
        return this.managers;
    }

    public static SslBundle get(Ssl ssl) throws NoSuchSslBundleException {
        return get(ssl, null, null);
    }

    public static SslBundle get(Ssl ssl, SslBundles sslBundles) throws NoSuchSslBundleException {
        return get(ssl, sslBundles, null);
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public static SslBundle get(Ssl ssl, SslBundles sslBundles, SslStoreProvider sslStoreProvider) {
        Assert.state(Ssl.isEnabled(ssl), "SSL is not enabled");
        String keyPassword = sslStoreProvider != null ? sslStoreProvider.getKeyPassword() : null;
        String keyPassword2 = keyPassword != null ? keyPassword : ssl.getKeyPassword();
        if (sslStoreProvider != null) {
            SslStoreBundle stores = new SslStoreProviderBundleAdapter(sslStoreProvider);
            return new WebServerSslBundle(stores, keyPassword2, ssl);
        }
        String bundleName = ssl.getBundle();
        if (StringUtils.hasText(bundleName)) {
            Assert.state(sslBundles != null, (Supplier<String>) () -> {
                return "SSL bundle '%s' was requested but no SslBundles instance was provided".formatted(bundleName);
            });
            return sslBundles.getBundle(bundleName);
        }
        SslStoreBundle stores2 = createStoreBundle(ssl);
        return new WebServerSslBundle(stores2, keyPassword2, ssl);
    }

    private static SslStoreBundle createStoreBundle(Ssl ssl) {
        KeyStore keyStore = createKeyStore(ssl);
        KeyStore trustStore = createTrustStore(ssl);
        return new WebServerSslStoreBundle(keyStore, trustStore, ssl.getKeyStorePassword());
    }

    private static KeyStore createKeyStore(Ssl ssl) {
        if (hasPemKeyStoreProperties(ssl)) {
            return createPemKeyStoreBundle(ssl).getKeyStore();
        }
        if (hasJksKeyStoreProperties(ssl)) {
            return createJksKeyStoreBundle(ssl).getKeyStore();
        }
        return null;
    }

    private static KeyStore createTrustStore(Ssl ssl) {
        if (hasPemTrustStoreProperties(ssl)) {
            return createPemTrustStoreBundle(ssl).getTrustStore();
        }
        if (hasJksTrustStoreProperties(ssl)) {
            return createJksTrustStoreBundle(ssl).getTrustStore();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SslBundle createCertificateFileSslStoreProviderDelegate(Ssl ssl) {
        if (!hasPemKeyStoreProperties(ssl)) {
            return null;
        }
        SslStoreBundle stores = createPemStoreBundle(ssl);
        return new WebServerSslBundle(stores, ssl.getKeyPassword(), ssl);
    }

    private static boolean hasPemKeyStoreProperties(Ssl ssl) {
        return (!Ssl.isEnabled(ssl) || ssl.getCertificate() == null || ssl.getCertificatePrivateKey() == null) ? false : true;
    }

    private static boolean hasPemTrustStoreProperties(Ssl ssl) {
        return Ssl.isEnabled(ssl) && ssl.getTrustCertificate() != null;
    }

    private static boolean hasJksKeyStoreProperties(Ssl ssl) {
        return Ssl.isEnabled(ssl) && (ssl.getKeyStore() != null || (ssl.getKeyStoreType() != null && ssl.getKeyStoreType().equals("PKCS11")));
    }

    private static boolean hasJksTrustStoreProperties(Ssl ssl) {
        return Ssl.isEnabled(ssl) && (ssl.getTrustStore() != null || (ssl.getTrustStoreType() != null && ssl.getTrustStoreType().equals("PKCS11")));
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("key", this.key);
        creator.append("protocol", this.protocol);
        creator.append("stores", this.stores);
        creator.append("options", this.options);
        return creator.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/WebServerSslBundle$SslStoreProviderBundleAdapter.class */
    public static class SslStoreProviderBundleAdapter implements SslStoreBundle {
        private final SslStoreProvider sslStoreProvider;

        SslStoreProviderBundleAdapter(SslStoreProvider sslStoreProvider) {
            this.sslStoreProvider = sslStoreProvider;
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public KeyStore getKeyStore() {
            SslStoreProvider sslStoreProvider = this.sslStoreProvider;
            Objects.requireNonNull(sslStoreProvider);
            return (KeyStore) ThrowingSupplier.of(sslStoreProvider::getKeyStore).get();
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public String getKeyStorePassword() {
            return null;
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public KeyStore getTrustStore() {
            SslStoreProvider sslStoreProvider = this.sslStoreProvider;
            Objects.requireNonNull(sslStoreProvider);
            return (KeyStore) ThrowingSupplier.of(sslStoreProvider::getTrustStore).get();
        }

        public String toString() {
            ToStringCreator creator = new ToStringCreator(this);
            KeyStore keyStore = getKeyStore();
            creator.append("keyStore.type", keyStore != null ? keyStore.getType() : "none");
            creator.append("keyStorePassword", (Object) null);
            KeyStore trustStore = getTrustStore();
            creator.append("trustStore.type", trustStore != null ? trustStore.getType() : "none");
            return creator.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/WebServerSslBundle$WebServerSslStoreBundle.class */
    public static final class WebServerSslStoreBundle implements SslStoreBundle {
        private final KeyStore keyStore;
        private final KeyStore trustStore;
        private final String keyStorePassword;

        private WebServerSslStoreBundle(KeyStore keyStore, KeyStore trustStore, String keyStorePassword) {
            Assert.state((keyStore == null && trustStore == null) ? false : true, "SSL is enabled but no trust material is configured");
            this.keyStore = keyStore;
            this.trustStore = trustStore;
            this.keyStorePassword = keyStorePassword;
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public KeyStore getKeyStore() {
            return this.keyStore;
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public KeyStore getTrustStore() {
            return this.trustStore;
        }

        @Override // org.springframework.boot.ssl.SslStoreBundle
        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public String toString() {
            ToStringCreator creator = new ToStringCreator(this);
            creator.append("keyStore.type", this.keyStore != null ? this.keyStore.getType() : "none");
            creator.append("keyStorePassword", this.keyStorePassword != null ? "******" : null);
            creator.append("trustStore.type", this.trustStore != null ? this.trustStore.getType() : "none");
            return creator.toString();
        }
    }
}
