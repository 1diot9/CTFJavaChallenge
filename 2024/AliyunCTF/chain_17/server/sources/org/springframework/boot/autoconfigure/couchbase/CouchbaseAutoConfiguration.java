package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.json.JsonValueModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({CouchbaseProperties.class})
@AutoConfiguration(after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({Cluster.class})
@Conditional({CouchbaseCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration.class */
public class CouchbaseAutoConfiguration {
    private final CouchbaseProperties properties;

    CouchbaseAutoConfiguration(CouchbaseProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({CouchbaseConnectionDetails.class})
    @Bean
    PropertiesCouchbaseConnectionDetails couchbaseConnectionDetails() {
        return new PropertiesCouchbaseConnectionDetails(this.properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public ClusterEnvironment couchbaseClusterEnvironment(CouchbaseConnectionDetails connectionDetails, ObjectProvider<ClusterEnvironmentBuilderCustomizer> customizers, ObjectProvider<SslBundles> sslBundles) {
        ClusterEnvironment.Builder builder = initializeEnvironmentBuilder(connectionDetails, sslBundles.getIfAvailable());
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "disconnect")
    public Cluster couchbaseCluster(ClusterEnvironment couchbaseClusterEnvironment, CouchbaseConnectionDetails connectionDetails) {
        ClusterOptions options = ClusterOptions.clusterOptions(connectionDetails.getUsername(), connectionDetails.getPassword()).environment(couchbaseClusterEnvironment);
        return Cluster.connect(connectionDetails.getConnectionString(), options);
    }

    private ClusterEnvironment.Builder initializeEnvironmentBuilder(CouchbaseConnectionDetails connectionDetails, SslBundles sslBundles) {
        ClusterEnvironment.Builder builder = ClusterEnvironment.builder();
        CouchbaseProperties.Timeouts timeouts = this.properties.getEnv().getTimeouts();
        builder.timeoutConfig(config -> {
            config.kvTimeout(timeouts.getKeyValue()).analyticsTimeout(timeouts.getAnalytics()).kvDurableTimeout(timeouts.getKeyValueDurable()).queryTimeout(timeouts.getQuery()).viewTimeout(timeouts.getView()).searchTimeout(timeouts.getSearch()).managementTimeout(timeouts.getManagement()).connectTimeout(timeouts.getConnect()).disconnectTimeout(timeouts.getDisconnect());
        });
        CouchbaseProperties.Io io2 = this.properties.getEnv().getIo();
        builder.ioConfig(config2 -> {
            config2.maxHttpConnections(io2.getMaxEndpoints()).numKvConnections(io2.getMinEndpoints()).idleHttpConnectionTimeout(io2.getIdleHttpConnectionTimeout());
        });
        if (this.properties.getEnv().getSsl().getEnabled().booleanValue()) {
            configureSsl(builder, sslBundles);
        }
        return builder;
    }

    private void configureSsl(ClusterEnvironment.Builder builder, SslBundles sslBundles) {
        CouchbaseProperties.Ssl sslProperties = this.properties.getEnv().getSsl();
        SslBundle sslBundle = StringUtils.hasText(sslProperties.getBundle()) ? sslBundles.getBundle(sslProperties.getBundle()) : null;
        Assert.state(sslBundle == null || !sslBundle.getOptions().isSpecified(), "SSL Options cannot be specified with Couchbase");
        builder.securityConfig(config -> {
            config.enableTls(true);
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(sslProperties, sslBundle);
            if (trustManagerFactory != null) {
                config.trustManagerFactory(trustManagerFactory);
            }
        });
    }

    private TrustManagerFactory getTrustManagerFactory(CouchbaseProperties.Ssl sslProperties, SslBundle sslBundle) {
        if (sslProperties.getKeyStore() != null) {
            return loadTrustManagerFactory(sslProperties);
        }
        if (sslBundle != null) {
            return sslBundle.getManagers().getTrustManagerFactory();
        }
        return null;
    }

    private TrustManagerFactory loadTrustManagerFactory(CouchbaseProperties.Ssl ssl) {
        String resource = ssl.getKeyStore();
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = loadKeyStore(resource, ssl.getKeyStorePassword());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load Couchbase key store '" + resource + "'", ex);
        }
    }

    private KeyStore loadKeyStore(String resource, String keyStorePassword) throws Exception {
        char[] charArray;
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        URL url = ResourceUtils.getURL(resource);
        InputStream stream = url.openStream();
        if (keyStorePassword != null) {
            try {
                charArray = keyStorePassword.toCharArray();
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } else {
            charArray = null;
        }
        store.load(stream, charArray);
        if (stream != null) {
            stream.close();
        }
        return store;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$JacksonConfiguration.class */
    static class JacksonConfiguration {
        JacksonConfiguration() {
        }

        @Bean
        @ConditionalOnSingleCandidate(ObjectMapper.class)
        ClusterEnvironmentBuilderCustomizer jacksonClusterEnvironmentBuilderCustomizer(ObjectMapper objectMapper) {
            return new JacksonClusterEnvironmentBuilderCustomizer(objectMapper.copy().registerModule(new JsonValueModule()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$JacksonClusterEnvironmentBuilderCustomizer.class */
    private static final class JacksonClusterEnvironmentBuilderCustomizer implements ClusterEnvironmentBuilderCustomizer, Ordered {
        private final ObjectMapper objectMapper;

        private JacksonClusterEnvironmentBuilderCustomizer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override // org.springframework.boot.autoconfigure.couchbase.ClusterEnvironmentBuilderCustomizer
        public void customize(ClusterEnvironment.Builder builder) {
            builder.jsonSerializer(JacksonJsonSerializer.create(this.objectMapper));
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition.class */
    static final class CouchbaseCondition extends AnyNestedCondition {
        CouchbaseCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "spring.couchbase", name = {"connection-string"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition$CouchbaseUrlCondition.class */
        private static final class CouchbaseUrlCondition {
            private CouchbaseUrlCondition() {
            }
        }

        @ConditionalOnBean({CouchbaseConnectionDetails.class})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition$CouchbaseConnectionDetailsCondition.class */
        private static final class CouchbaseConnectionDetailsCondition {
            private CouchbaseConnectionDetailsCondition() {
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$PropertiesCouchbaseConnectionDetails.class */
    static final class PropertiesCouchbaseConnectionDetails implements CouchbaseConnectionDetails {
        private final CouchbaseProperties properties;

        PropertiesCouchbaseConnectionDetails(CouchbaseProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.couchbase.CouchbaseConnectionDetails
        public String getConnectionString() {
            return this.properties.getConnectionString();
        }

        @Override // org.springframework.boot.autoconfigure.couchbase.CouchbaseConnectionDetails
        public String getUsername() {
            return this.properties.getUsername();
        }

        @Override // org.springframework.boot.autoconfigure.couchbase.CouchbaseConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }
    }
}
