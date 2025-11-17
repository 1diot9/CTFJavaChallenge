package org.springframework.boot.ssl.pem;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/PemSslStoreBundle.class */
public class PemSslStoreBundle implements SslStoreBundle {
    private static final String DEFAULT_ALIAS = "ssl";
    private final KeyStore keyStore;
    private final KeyStore trustStore;

    public PemSslStoreBundle(PemSslStoreDetails keyStoreDetails, PemSslStoreDetails trustStoreDetails) {
        this(keyStoreDetails, trustStoreDetails, (String) null);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public PemSslStoreBundle(PemSslStoreDetails keyStoreDetails, PemSslStoreDetails trustStoreDetails, String alias) {
        this.keyStore = createKeyStore("key", PemSslStore.load(keyStoreDetails), alias);
        this.trustStore = createKeyStore("trust", PemSslStore.load(trustStoreDetails), alias);
    }

    public PemSslStoreBundle(PemSslStore pemKeyStore, PemSslStore pemTrustStore) {
        this(pemKeyStore, pemTrustStore, (String) null);
    }

    private PemSslStoreBundle(PemSslStore pemKeyStore, PemSslStore pemTrustStore, String alias) {
        this.keyStore = createKeyStore("key", pemKeyStore, alias);
        this.trustStore = createKeyStore("trust", pemTrustStore, alias);
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public String getKeyStorePassword() {
        return null;
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public KeyStore getTrustStore() {
        return this.trustStore;
    }

    private static KeyStore createKeyStore(String name, PemSslStore pemSslStore, String alias) {
        if (pemSslStore == null) {
            return null;
        }
        try {
            Assert.notEmpty(pemSslStore.certificates(), "Certificates must not be empty");
            String alias2 = pemSslStore.alias() != null ? pemSslStore.alias() : alias;
            String alias3 = alias2 != null ? alias2 : DEFAULT_ALIAS;
            KeyStore store = createKeyStore(pemSslStore.type());
            List<X509Certificate> certificates = pemSslStore.certificates();
            PrivateKey privateKey = pemSslStore.privateKey();
            if (privateKey != null) {
                addPrivateKey(store, privateKey, alias3, pemSslStore.password(), certificates);
            } else {
                addCertificates(store, certificates, alias3);
            }
            return store;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create %s store: %s".formatted(name, ex.getMessage()), ex);
        }
    }

    private static KeyStore createKeyStore(String type) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore store = KeyStore.getInstance(StringUtils.hasText(type) ? type : KeyStore.getDefaultType());
        store.load(null);
        return store;
    }

    private static void addPrivateKey(KeyStore keyStore, PrivateKey privateKey, String alias, String keyPassword, List<X509Certificate> certificateChain) throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, keyPassword != null ? keyPassword.toCharArray() : null, (Certificate[]) certificateChain.toArray(x$0 -> {
            return new X509Certificate[x$0];
        }));
    }

    private static void addCertificates(KeyStore keyStore, List<X509Certificate> certificates, String alias) throws KeyStoreException {
        for (int index = 0; index < certificates.size(); index++) {
            String entryAlias = alias + (certificates.size() == 1 ? "" : "-" + index);
            X509Certificate certificate = certificates.get(index);
            keyStore.setCertificateEntry(entryAlias, certificate);
        }
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("keyStore.type", this.keyStore != null ? this.keyStore.getType() : "none");
        creator.append("keyStorePassword", (Object) null);
        creator.append("trustStore.type", this.trustStore != null ? this.trustStore.getType() : "none");
        return creator.toString();
    }
}
