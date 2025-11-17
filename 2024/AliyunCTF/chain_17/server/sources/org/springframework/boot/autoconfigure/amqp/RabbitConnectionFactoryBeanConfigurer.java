package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import java.util.Objects;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitConnectionFactoryBeanConfigurer.class */
public class RabbitConnectionFactoryBeanConfigurer {
    private final RabbitProperties rabbitProperties;
    private final ResourceLoader resourceLoader;
    private final RabbitConnectionDetails connectionDetails;
    private final SslBundles sslBundles;
    private CredentialsProvider credentialsProvider;
    private CredentialsRefreshService credentialsRefreshService;

    public RabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader, RabbitProperties properties) {
        this(resourceLoader, properties, new PropertiesRabbitConnectionDetails(properties));
    }

    public RabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader, RabbitProperties properties, RabbitConnectionDetails connectionDetails) {
        this(resourceLoader, properties, connectionDetails, null);
    }

    public RabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader, RabbitProperties properties, RabbitConnectionDetails connectionDetails, SslBundles sslBundles) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        Assert.notNull(properties, "Properties must not be null");
        Assert.notNull(connectionDetails, "ConnectionDetails must not be null");
        this.resourceLoader = resourceLoader;
        this.rabbitProperties = properties;
        this.connectionDetails = connectionDetails;
        this.sslBundles = sslBundles;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setCredentialsRefreshService(CredentialsRefreshService credentialsRefreshService) {
        this.credentialsRefreshService = credentialsRefreshService;
    }

    public void configure(RabbitConnectionFactoryBean factory) {
        Assert.notNull(factory, "RabbitConnectionFactoryBean must not be null");
        factory.setResourceLoader(this.resourceLoader);
        RabbitConnectionDetails.Address address = this.connectionDetails.getFirstAddress();
        PropertyMapper map = PropertyMapper.get();
        Objects.requireNonNull(address);
        PropertyMapper.Source whenNonNull = map.from(address::host).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull.to(factory::setHost);
        Objects.requireNonNull(address);
        PropertyMapper.Source from = map.from(address::port);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setPort(v1);
        });
        RabbitConnectionDetails rabbitConnectionDetails = this.connectionDetails;
        Objects.requireNonNull(rabbitConnectionDetails);
        PropertyMapper.Source whenNonNull2 = map.from(rabbitConnectionDetails::getUsername).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull2.to(factory::setUsername);
        RabbitConnectionDetails rabbitConnectionDetails2 = this.connectionDetails;
        Objects.requireNonNull(rabbitConnectionDetails2);
        PropertyMapper.Source whenNonNull3 = map.from(rabbitConnectionDetails2::getPassword).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull3.to(factory::setPassword);
        RabbitConnectionDetails rabbitConnectionDetails3 = this.connectionDetails;
        Objects.requireNonNull(rabbitConnectionDetails3);
        PropertyMapper.Source whenNonNull4 = map.from(rabbitConnectionDetails3::getVirtualHost).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull4.to(factory::setVirtualHost);
        RabbitProperties rabbitProperties = this.rabbitProperties;
        Objects.requireNonNull(rabbitProperties);
        PropertyMapper.Source<Integer> asInt = map.from(rabbitProperties::getRequestedHeartbeat).whenNonNull().asInt((v0) -> {
            return v0.getSeconds();
        });
        Objects.requireNonNull(factory);
        asInt.to((v1) -> {
            r1.setRequestedHeartbeat(v1);
        });
        RabbitProperties rabbitProperties2 = this.rabbitProperties;
        Objects.requireNonNull(rabbitProperties2);
        PropertyMapper.Source from2 = map.from(rabbitProperties2::getRequestedChannelMax);
        Objects.requireNonNull(factory);
        from2.to((v1) -> {
            r1.setRequestedChannelMax(v1);
        });
        RabbitProperties.Ssl ssl = this.rabbitProperties.getSsl();
        if (ssl.determineEnabled()) {
            factory.setUseSSL(true);
            if (ssl.getBundle() != null) {
                SslBundle bundle = this.sslBundles.getBundle(ssl.getBundle());
                if (factory instanceof SslBundleRabbitConnectionFactoryBean) {
                    SslBundleRabbitConnectionFactoryBean sslFactory = (SslBundleRabbitConnectionFactoryBean) factory;
                    sslFactory.setSslBundle(bundle);
                }
            } else {
                Objects.requireNonNull(ssl);
                PropertyMapper.Source whenNonNull5 = map.from(ssl::getAlgorithm).whenNonNull();
                Objects.requireNonNull(factory);
                whenNonNull5.to(factory::setSslAlgorithm);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from3 = map.from(ssl::getKeyStoreType);
                Objects.requireNonNull(factory);
                from3.to(factory::setKeyStoreType);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from4 = map.from(ssl::getKeyStore);
                Objects.requireNonNull(factory);
                from4.to(factory::setKeyStore);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from5 = map.from(ssl::getKeyStorePassword);
                Objects.requireNonNull(factory);
                from5.to(factory::setKeyStorePassphrase);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source whenNonNull6 = map.from(ssl::getKeyStoreAlgorithm).whenNonNull();
                Objects.requireNonNull(factory);
                whenNonNull6.to(factory::setKeyStoreAlgorithm);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from6 = map.from(ssl::getTrustStoreType);
                Objects.requireNonNull(factory);
                from6.to(factory::setTrustStoreType);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from7 = map.from(ssl::getTrustStore);
                Objects.requireNonNull(factory);
                from7.to(factory::setTrustStore);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source from8 = map.from(ssl::getTrustStorePassword);
                Objects.requireNonNull(factory);
                from8.to(factory::setTrustStorePassphrase);
                Objects.requireNonNull(ssl);
                PropertyMapper.Source whenNonNull7 = map.from(ssl::getTrustStoreAlgorithm).whenNonNull();
                Objects.requireNonNull(factory);
                whenNonNull7.to(factory::setTrustStoreAlgorithm);
            }
            Objects.requireNonNull(ssl);
            map.from(ssl::isValidateServerCertificate).to(validate -> {
                factory.setSkipServerCertificateValidation(!validate.booleanValue());
            });
            Objects.requireNonNull(ssl);
            PropertyMapper.Source from9 = map.from(ssl::getVerifyHostname);
            Objects.requireNonNull(factory);
            from9.to((v1) -> {
                r1.setEnableHostnameVerification(v1);
            });
        }
        RabbitProperties rabbitProperties3 = this.rabbitProperties;
        Objects.requireNonNull(rabbitProperties3);
        PropertyMapper.Source<Integer> asInt2 = map.from(rabbitProperties3::getConnectionTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(factory);
        asInt2.to((v1) -> {
            r1.setConnectionTimeout(v1);
        });
        RabbitProperties rabbitProperties4 = this.rabbitProperties;
        Objects.requireNonNull(rabbitProperties4);
        PropertyMapper.Source<Integer> asInt3 = map.from(rabbitProperties4::getChannelRpcTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(factory);
        asInt3.to((v1) -> {
            r1.setChannelRpcTimeout(v1);
        });
        PropertyMapper.Source whenNonNull8 = map.from((PropertyMapper) this.credentialsProvider).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull8.to(factory::setCredentialsProvider);
        PropertyMapper.Source whenNonNull9 = map.from((PropertyMapper) this.credentialsRefreshService).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull9.to(factory::setCredentialsRefreshService);
        PropertyMapper.Source<Integer> asInt4 = map.from((PropertyMapper) this.rabbitProperties.getMaxInboundMessageBodySize()).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        });
        Objects.requireNonNull(factory);
        asInt4.to((v1) -> {
            r1.setMaxInboundMessageBodySize(v1);
        });
    }
}
