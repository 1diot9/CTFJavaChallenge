package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.slf4j.Marker;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.web.server.Ssl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultBindingErrorProcessor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/SslConnectorCustomizer.class */
public class SslConnectorCustomizer {
    private final Log logger;
    private final Ssl.ClientAuth clientAuth;
    private final Connector connector;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslConnectorCustomizer(Log logger, Connector connector, Ssl.ClientAuth clientAuth) {
        this.logger = logger;
        this.clientAuth = clientAuth;
        this.connector = connector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void update(SslBundle updatedSslBundle) {
        this.logger.debug("SSL Bundle has been updated, reloading SSL configuration");
        customize(updatedSslBundle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customize(SslBundle sslBundle) {
        ProtocolHandler handler = this.connector.getProtocolHandler();
        Assert.state(handler instanceof AbstractHttp11JsseProtocol, "To use SSL, the connector's protocol handler must be an AbstractHttp11JsseProtocol subclass");
        configureSsl(sslBundle, (AbstractHttp11JsseProtocol) handler);
        this.connector.setScheme("https");
        this.connector.setSecure(true);
    }

    private void configureSsl(SslBundle sslBundle, AbstractHttp11JsseProtocol<?> protocol) {
        protocol.setSSLEnabled(true);
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setHostName(protocol.getDefaultSSLHostConfigName());
        configureSslClientAuth(sslHostConfig);
        applySslBundle(sslBundle, protocol, sslHostConfig);
        protocol.addSslHostConfig(sslHostConfig, true);
    }

    private void applySslBundle(SslBundle sslBundle, AbstractHttp11JsseProtocol<?> protocol, SSLHostConfig sslHostConfig) {
        SslBundleKey key = sslBundle.getKey();
        SslStoreBundle stores = sslBundle.getStores();
        SslOptions options = sslBundle.getOptions();
        sslHostConfig.setSslProtocol(sslBundle.getProtocol());
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.UNDEFINED);
        String keystorePassword = stores.getKeyStorePassword() != null ? stores.getKeyStorePassword() : "";
        certificate.setCertificateKeystorePassword(keystorePassword);
        if (key.getPassword() != null) {
            certificate.setCertificateKeyPassword(key.getPassword());
        }
        if (key.getAlias() != null) {
            certificate.setCertificateKeyAlias(key.getAlias());
        }
        sslHostConfig.addCertificate(certificate);
        if (options.getCiphers() != null) {
            String ciphers = StringUtils.arrayToCommaDelimitedString(options.getCiphers());
            sslHostConfig.setCiphers(ciphers);
        }
        configureSslStoreProvider(protocol, sslHostConfig, certificate, stores);
        configureEnabledProtocols(sslHostConfig, options);
    }

    private void configureEnabledProtocols(SSLHostConfig sslHostConfig, SslOptions options) {
        if (options.getEnabledProtocols() != null) {
            String enabledProtocols = StringUtils.arrayToDelimitedString(options.getEnabledProtocols(), Marker.ANY_NON_NULL_MARKER);
            sslHostConfig.setProtocols(enabledProtocols);
        }
    }

    private void configureSslClientAuth(SSLHostConfig config) {
        config.setCertificateVerification((String) Ssl.ClientAuth.map(this.clientAuth, "none", "optional", DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE));
    }

    private void configureSslStoreProvider(AbstractHttp11JsseProtocol<?> protocol, SSLHostConfig sslHostConfig, SSLHostConfigCertificate certificate, SslStoreBundle stores) {
        Assert.isInstanceOf((Class<?>) Http11NioProtocol.class, protocol, "SslStoreProvider can only be used with Http11NioProtocol");
        try {
            if (stores.getKeyStore() != null) {
                certificate.setCertificateKeystore(stores.getKeyStore());
            }
            if (stores.getTrustStore() != null) {
                sslHostConfig.setTrustStore(stores.getTrustStore());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load store: " + ex.getMessage(), ex);
        }
    }
}
