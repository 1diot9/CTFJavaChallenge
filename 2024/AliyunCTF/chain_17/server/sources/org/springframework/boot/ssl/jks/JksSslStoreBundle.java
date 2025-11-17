package org.springframework.boot.ssl.jks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.function.Supplier;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/jks/JksSslStoreBundle.class */
public class JksSslStoreBundle implements SslStoreBundle {
    private final JksSslStoreDetails keyStoreDetails;
    private final KeyStore keyStore;
    private final KeyStore trustStore;

    public JksSslStoreBundle(JksSslStoreDetails keyStoreDetails, JksSslStoreDetails trustStoreDetails) {
        this.keyStoreDetails = keyStoreDetails;
        this.keyStore = createKeyStore("key", this.keyStoreDetails);
        this.trustStore = createKeyStore("trust", trustStoreDetails);
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public String getKeyStorePassword() {
        if (this.keyStoreDetails != null) {
            return this.keyStoreDetails.password();
        }
        return null;
    }

    @Override // org.springframework.boot.ssl.SslStoreBundle
    public KeyStore getTrustStore() {
        return this.trustStore;
    }

    private KeyStore createKeyStore(String name, JksSslStoreDetails details) {
        if (details == null || details.isEmpty()) {
            return null;
        }
        try {
            String type = !StringUtils.hasText(details.type()) ? KeyStore.getDefaultType() : details.type();
            char[] password = details.password() != null ? details.password().toCharArray() : null;
            String location = details.location();
            KeyStore store = getKeyStoreInstance(type, details.provider());
            if (isHardwareKeystoreType(type)) {
                loadHardwareKeyStore(store, location, password);
            } else {
                loadKeyStore(store, location, password);
            }
            return store;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create %s store: %s".formatted(name, ex.getMessage()), ex);
        }
    }

    private KeyStore getKeyStoreInstance(String type, String provider) throws KeyStoreException, NoSuchProviderException {
        return !StringUtils.hasText(provider) ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
    }

    private boolean isHardwareKeystoreType(String type) {
        return type.equalsIgnoreCase("PKCS11");
    }

    private void loadHardwareKeyStore(KeyStore store, String location, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        Assert.state(!StringUtils.hasText(location), (Supplier<String>) () -> {
            return "Location is '%s', but must be empty or null for PKCS11 hardware key stores".formatted(location);
        });
        store.load(null, password);
    }

    private void loadKeyStore(KeyStore store, String location, char[] password) {
        Assert.state(StringUtils.hasText(location), (Supplier<String>) () -> {
            return "Location must not be empty or null";
        });
        try {
            URL url = ResourceUtils.getURL(location);
            InputStream stream = url.openStream();
            try {
                store.load(stream, password);
                if (stream != null) {
                    stream.close();
                }
            } finally {
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load store from '" + location + "'", ex);
        }
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("keyStore.type", this.keyStore != null ? this.keyStore.getType() : "none");
        String keyStorePassword = getKeyStorePassword();
        creator.append("keyStorePassword", keyStorePassword != null ? "******" : null);
        creator.append("trustStore.type", this.trustStore != null ? this.trustStore.getType() : "none");
        return creator.toString();
    }
}
