package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.spi.ContextAware;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/SSLContextFactoryBean.class */
public class SSLContextFactoryBean {
    private static final String JSSE_KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
    private static final String JSSE_TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
    private KeyStoreFactoryBean keyStore;
    private KeyStoreFactoryBean trustStore;
    private SecureRandomFactoryBean secureRandom;
    private KeyManagerFactoryFactoryBean keyManagerFactory;
    private TrustManagerFactoryFactoryBean trustManagerFactory;
    private String protocol;
    private String provider;

    public SSLContext createContext(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, CertificateException {
        SSLContext sslContext = getProvider() != null ? SSLContext.getInstance(getProtocol(), getProvider()) : SSLContext.getInstance(getProtocol());
        context.addInfo("SSL protocol '" + sslContext.getProtocol() + "' provider '" + String.valueOf(sslContext.getProvider()) + "'");
        KeyManager[] keyManagers = createKeyManagers(context);
        TrustManager[] trustManagers = createTrustManagers(context);
        SecureRandom secureRandom = createSecureRandom(context);
        sslContext.init(keyManagers, trustManagers, secureRandom);
        return sslContext;
    }

    private KeyManager[] createKeyManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        if (getKeyStore() == null) {
            return null;
        }
        KeyStore keyStore = getKeyStore().createKeyStore();
        context.addInfo("key store of type '" + keyStore.getType() + "' provider '" + String.valueOf(keyStore.getProvider()) + "': " + getKeyStore().getLocation());
        KeyManagerFactory kmf = getKeyManagerFactory().createKeyManagerFactory();
        context.addInfo("key manager algorithm '" + kmf.getAlgorithm() + "' provider '" + String.valueOf(kmf.getProvider()) + "'");
        char[] passphrase = getKeyStore().getPassword().toCharArray();
        kmf.init(keyStore, passphrase);
        return kmf.getKeyManagers();
    }

    private TrustManager[] createTrustManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
        if (getTrustStore() == null) {
            return null;
        }
        KeyStore trustStore = getTrustStore().createKeyStore();
        context.addInfo("trust store of type '" + trustStore.getType() + "' provider '" + String.valueOf(trustStore.getProvider()) + "': " + getTrustStore().getLocation());
        TrustManagerFactory tmf = getTrustManagerFactory().createTrustManagerFactory();
        context.addInfo("trust manager algorithm '" + tmf.getAlgorithm() + "' provider '" + String.valueOf(tmf.getProvider()) + "'");
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }

    private SecureRandom createSecureRandom(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException {
        SecureRandom secureRandom = getSecureRandom().createSecureRandom();
        context.addInfo("secure random algorithm '" + secureRandom.getAlgorithm() + "' provider '" + String.valueOf(secureRandom.getProvider()) + "'");
        return secureRandom;
    }

    public KeyStoreFactoryBean getKeyStore() {
        if (this.keyStore == null) {
            this.keyStore = keyStoreFromSystemProperties(JSSE_KEY_STORE_PROPERTY);
        }
        return this.keyStore;
    }

    public void setKeyStore(KeyStoreFactoryBean keyStore) {
        this.keyStore = keyStore;
    }

    public KeyStoreFactoryBean getTrustStore() {
        if (this.trustStore == null) {
            this.trustStore = keyStoreFromSystemProperties(JSSE_TRUST_STORE_PROPERTY);
        }
        return this.trustStore;
    }

    public void setTrustStore(KeyStoreFactoryBean trustStore) {
        this.trustStore = trustStore;
    }

    private KeyStoreFactoryBean keyStoreFromSystemProperties(String property) {
        if (System.getProperty(property) == null) {
            return null;
        }
        KeyStoreFactoryBean keyStore = new KeyStoreFactoryBean();
        keyStore.setLocation(locationFromSystemProperty(property));
        keyStore.setProvider(System.getProperty(property + "Provider"));
        keyStore.setPassword(System.getProperty(property + "Password"));
        keyStore.setType(System.getProperty(property + "Type"));
        return keyStore;
    }

    private String locationFromSystemProperty(String name) {
        String location = System.getProperty(name);
        if (location != null && !location.startsWith("file:")) {
            location = "file:" + location;
        }
        return location;
    }

    public SecureRandomFactoryBean getSecureRandom() {
        if (this.secureRandom == null) {
            return new SecureRandomFactoryBean();
        }
        return this.secureRandom;
    }

    public void setSecureRandom(SecureRandomFactoryBean secureRandom) {
        this.secureRandom = secureRandom;
    }

    public KeyManagerFactoryFactoryBean getKeyManagerFactory() {
        if (this.keyManagerFactory == null) {
            return new KeyManagerFactoryFactoryBean();
        }
        return this.keyManagerFactory;
    }

    public void setKeyManagerFactory(KeyManagerFactoryFactoryBean keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
    }

    public TrustManagerFactoryFactoryBean getTrustManagerFactory() {
        if (this.trustManagerFactory == null) {
            return new TrustManagerFactoryFactoryBean();
        }
        return this.trustManagerFactory;
    }

    public void setTrustManagerFactory(TrustManagerFactoryFactoryBean trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

    public String getProtocol() {
        if (this.protocol == null) {
            return "SSL";
        }
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
