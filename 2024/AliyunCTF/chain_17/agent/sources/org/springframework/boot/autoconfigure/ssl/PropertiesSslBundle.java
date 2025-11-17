package org.springframework.boot.autoconfigure.ssl;

import org.springframework.boot.autoconfigure.ssl.JksSslBundleProperties;
import org.springframework.boot.autoconfigure.ssl.PemSslBundleProperties;
import org.springframework.boot.autoconfigure.ssl.SslBundleProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslManagerBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.jks.JksSslStoreBundle;
import org.springframework.boot.ssl.jks.JksSslStoreDetails;
import org.springframework.boot.ssl.pem.PemSslStore;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/PropertiesSslBundle.class */
public final class PropertiesSslBundle implements SslBundle {
    private final SslStoreBundle stores;
    private final SslBundleKey key;
    private final SslOptions options;
    private final String protocol;
    private final SslManagerBundle managers;

    private PropertiesSslBundle(SslStoreBundle stores, SslBundleProperties properties) {
        this.stores = stores;
        this.key = asSslKeyReference(properties.getKey());
        this.options = asSslOptions(properties.getOptions());
        this.protocol = properties.getProtocol();
        this.managers = SslManagerBundle.from(this.stores, this.key);
    }

    private static SslBundleKey asSslKeyReference(SslBundleProperties.Key key) {
        return key != null ? SslBundleKey.of(key.getPassword(), key.getAlias()) : SslBundleKey.NONE;
    }

    private static SslOptions asSslOptions(SslBundleProperties.Options options) {
        return options != null ? SslOptions.of(options.getCiphers(), options.getEnabledProtocols()) : SslOptions.NONE;
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

    public static SslBundle get(PemSslBundleProperties properties) {
        PemSslStore keyStore = getPemSslStore("keystore", properties.getKeystore());
        if (keyStore != null) {
            keyStore = keyStore.withAlias(properties.getKey().getAlias()).withPassword(properties.getKey().getPassword());
        }
        PemSslStore trustStore = getPemSslStore("truststore", properties.getTruststore());
        SslStoreBundle storeBundle = new PemSslStoreBundle(keyStore, trustStore);
        return new PropertiesSslBundle(storeBundle, properties);
    }

    private static PemSslStore getPemSslStore(String propertyName, PemSslBundleProperties.Store properties) {
        PemSslStore pemSslStore = PemSslStore.load(asPemSslStoreDetails(properties));
        if (properties.isVerifyKeys()) {
            CertificateMatcher certificateMatcher = new CertificateMatcher(pemSslStore.privateKey());
            Assert.state(certificateMatcher.matchesAny(pemSslStore.certificates()), "Private key in %s matches none of the certificates in the chain".formatted(propertyName));
        }
        return pemSslStore;
    }

    private static PemSslStoreDetails asPemSslStoreDetails(PemSslBundleProperties.Store properties) {
        return new PemSslStoreDetails(properties.getType(), properties.getCertificate(), properties.getPrivateKey(), properties.getPrivateKeyPassword());
    }

    public static SslBundle get(JksSslBundleProperties properties) {
        SslStoreBundle storeBundle = asSslStoreBundle(properties);
        return new PropertiesSslBundle(storeBundle, properties);
    }

    private static SslStoreBundle asSslStoreBundle(JksSslBundleProperties properties) {
        JksSslStoreDetails keyStoreDetails = asStoreDetails(properties.getKeystore());
        JksSslStoreDetails trustStoreDetails = asStoreDetails(properties.getTruststore());
        return new JksSslStoreBundle(keyStoreDetails, trustStoreDetails);
    }

    private static JksSslStoreDetails asStoreDetails(JksSslBundleProperties.Store properties) {
        return new JksSslStoreDetails(properties.getType(), properties.getProvider(), properties.getLocation(), properties.getPassword());
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("key", this.key);
        creator.append("options", this.options);
        creator.append("protocol", this.protocol);
        creator.append("stores", this.stores);
        return creator.toString();
    }
}
