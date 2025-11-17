package org.springframework.boot.autoconfigure.kafka;

import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.kafka.common.security.auth.SslEngineFactory;
import org.springframework.boot.ssl.SslBundle;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/SslBundleSslEngineFactory.class */
public class SslBundleSslEngineFactory implements SslEngineFactory {
    private static final String SSL_BUNDLE_CONFIG_NAME = SslBundle.class.getName();
    private Map<String, ?> configs;
    private volatile SslBundle sslBundle;

    public void configure(Map<String, ?> configs) {
        this.configs = configs;
        this.sslBundle = (SslBundle) configs.get(SSL_BUNDLE_CONFIG_NAME);
    }

    public void close() throws IOException {
    }

    public SSLEngine createClientSslEngine(String peerHost, int peerPort, String endpointIdentification) {
        SSLEngine sslEngine = this.sslBundle.createSslContext().createSSLEngine(peerHost, peerPort);
        sslEngine.setUseClientMode(true);
        SSLParameters sslParams = sslEngine.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(endpointIdentification);
        sslEngine.setSSLParameters(sslParams);
        return sslEngine;
    }

    public SSLEngine createServerSslEngine(String peerHost, int peerPort) {
        SSLEngine sslEngine = this.sslBundle.createSslContext().createSSLEngine(peerHost, peerPort);
        sslEngine.setUseClientMode(false);
        return sslEngine;
    }

    public boolean shouldBeRebuilt(Map<String, Object> nextConfigs) {
        return !nextConfigs.equals(this.configs);
    }

    public Set<String> reconfigurableConfigs() {
        return Set.of(SSL_BUNDLE_CONFIG_NAME);
    }

    public KeyStore keystore() {
        return this.sslBundle.getStores().getKeyStore();
    }

    public KeyStore truststore() {
        return this.sslBundle.getStores().getTrustStore();
    }
}
