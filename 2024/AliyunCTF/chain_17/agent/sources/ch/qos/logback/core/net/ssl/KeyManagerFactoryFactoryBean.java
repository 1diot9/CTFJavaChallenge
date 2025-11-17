package ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.net.ssl.KeyManagerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/KeyManagerFactoryFactoryBean.class */
public class KeyManagerFactoryFactoryBean {
    private String algorithm;
    private String provider;

    public KeyManagerFactory createKeyManagerFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
        return getProvider() != null ? KeyManagerFactory.getInstance(getAlgorithm(), getProvider()) : KeyManagerFactory.getInstance(getAlgorithm());
    }

    public String getAlgorithm() {
        if (this.algorithm == null) {
            return KeyManagerFactory.getDefaultAlgorithm();
        }
        return this.algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
