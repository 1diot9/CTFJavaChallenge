package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.DriverOption;
import com.datastax.oss.driver.api.core.ssl.ProgrammaticSslEngineFactory;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({CassandraProperties.class})
@AutoConfiguration
@ConditionalOnClass({CqlSession.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration.class */
public class CassandraAutoConfiguration {
    private static final Config SPRING_BOOT_DEFAULTS;
    private final CassandraProperties properties;

    static {
        CassandraDriverOptions options = new CassandraDriverOptions();
        options.add((DriverOption) DefaultDriverOption.CONTACT_POINTS, Collections.singletonList("127.0.0.1:9042"));
        options.add((DriverOption) DefaultDriverOption.PROTOCOL_COMPRESSION, "none");
        options.add((DriverOption) DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT, (int) Duration.ofSeconds(5L).toMillis());
        SPRING_BOOT_DEFAULTS = options.build();
    }

    CassandraAutoConfiguration(CassandraProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({CassandraConnectionDetails.class})
    @Bean
    PropertiesCassandraConnectionDetails cassandraConnectionDetails() {
        return new PropertiesCassandraConnectionDetails(this.properties);
    }

    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public CqlSession cassandraSession(CqlSessionBuilder cqlSessionBuilder) {
        return (CqlSession) cqlSessionBuilder.build();
    }

    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public CqlSessionBuilder cassandraSessionBuilder(DriverConfigLoader driverConfigLoader, CassandraConnectionDetails connectionDetails, ObjectProvider<CqlSessionBuilderCustomizer> builderCustomizers, ObjectProvider<SslBundles> sslBundles) {
        CqlSessionBuilder builder = (CqlSessionBuilder) CqlSession.builder().withConfigLoader(driverConfigLoader);
        configureAuthentication(builder, connectionDetails);
        configureSsl(builder, sslBundles.getIfAvailable());
        builder.withKeyspace(this.properties.getKeyspaceName());
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    private void configureAuthentication(CqlSessionBuilder builder, CassandraConnectionDetails connectionDetails) {
        String username = connectionDetails.getUsername();
        if (username != null) {
            builder.withAuthCredentials(username, connectionDetails.getPassword());
        }
    }

    private void configureSsl(CqlSessionBuilder builder, SslBundles sslBundles) {
        CassandraProperties.Ssl properties = this.properties.getSsl();
        if (properties == null || !properties.isEnabled()) {
            return;
        }
        String bundleName = properties.getBundle();
        if (!StringUtils.hasLength(bundleName)) {
            configureDefaultSslContext(builder);
        } else {
            configureSsl(builder, sslBundles.getBundle(bundleName));
        }
    }

    private void configureDefaultSslContext(CqlSessionBuilder builder) {
        try {
            builder.withSslContext(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not setup SSL default context for Cassandra", ex);
        }
    }

    private void configureSsl(CqlSessionBuilder builder, SslBundle sslBundle) {
        SslOptions options = sslBundle.getOptions();
        Assert.state(options.getEnabledProtocols() == null, "SSL protocol options cannot be specified with Cassandra");
        builder.withSslEngineFactory(new ProgrammaticSslEngineFactory(sslBundle.createSslContext(), options.getCiphers()));
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "")
    public DriverConfigLoader cassandraDriverConfigLoader(CassandraConnectionDetails connectionDetails, ObjectProvider<DriverConfigLoaderBuilderCustomizer> builderCustomizers) {
        DefaultProgrammaticDriverConfigLoaderBuilder defaultProgrammaticDriverConfigLoaderBuilder = new DefaultProgrammaticDriverConfigLoaderBuilder(() -> {
            return cassandraConfiguration(connectionDetails);
        }, "datastax-java-driver");
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(defaultProgrammaticDriverConfigLoaderBuilder);
        });
        return defaultProgrammaticDriverConfigLoaderBuilder.build();
    }

    private Config cassandraConfiguration(CassandraConnectionDetails connectionDetails) {
        ConfigFactory.invalidateCaches();
        Config config = ConfigFactory.defaultOverrides().withFallback(mapConfig(connectionDetails));
        if (this.properties.getConfig() != null) {
            config = config.withFallback(loadConfig(this.properties.getConfig()));
        }
        return config.withFallback(SPRING_BOOT_DEFAULTS).withFallback(ConfigFactory.defaultReferenceUnresolved()).resolve();
    }

    private Config loadConfig(Resource resource) {
        try {
            return ConfigFactory.parseURL(resource.getURL());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load cassandra configuration from " + resource, ex);
        }
    }

    private Config mapConfig(CassandraConnectionDetails connectionDetails) {
        CassandraDriverOptions options = new CassandraDriverOptions();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from((PropertyMapper) this.properties.getSessionName()).whenHasText().to(sessionName -> {
            options.add((DriverOption) DefaultDriverOption.SESSION_NAME, sessionName);
        });
        map.from((PropertyMapper) connectionDetails.getUsername()).to(value -> {
            options.add((DriverOption) DefaultDriverOption.AUTH_PROVIDER_USER_NAME, value).add((DriverOption) DefaultDriverOption.AUTH_PROVIDER_PASSWORD, connectionDetails.getPassword());
        });
        CassandraProperties cassandraProperties = this.properties;
        Objects.requireNonNull(cassandraProperties);
        map.from(cassandraProperties::getCompression).to(compression -> {
            options.add((DriverOption) DefaultDriverOption.PROTOCOL_COMPRESSION, (Enum<?>) compression);
        });
        mapConnectionOptions(options);
        mapPoolingOptions(options);
        mapRequestOptions(options);
        mapControlConnectionOptions(options);
        map.from((PropertyMapper) mapContactPoints(connectionDetails)).to(contactPoints -> {
            options.add((DriverOption) DefaultDriverOption.CONTACT_POINTS, (List<String>) contactPoints);
        });
        map.from((PropertyMapper) connectionDetails.getLocalDatacenter()).whenHasText().to(localDatacenter -> {
            options.add((DriverOption) DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDatacenter);
        });
        return options.build();
    }

    private void mapConnectionOptions(CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Connection connectionProperties = this.properties.getConnection();
        Objects.requireNonNull(connectionProperties);
        map.from(connectionProperties::getConnectTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(connectTimeout -> {
            options.add((DriverOption) DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, connectTimeout.intValue());
        });
        Objects.requireNonNull(connectionProperties);
        map.from(connectionProperties::getInitQueryTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(initQueryTimeout -> {
            options.add((DriverOption) DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, initQueryTimeout.intValue());
        });
    }

    private void mapPoolingOptions(CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Pool poolProperties = this.properties.getPool();
        Objects.requireNonNull(poolProperties);
        map.from(poolProperties::getIdleTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(idleTimeout -> {
            options.add((DriverOption) DefaultDriverOption.HEARTBEAT_TIMEOUT, idleTimeout.intValue());
        });
        Objects.requireNonNull(poolProperties);
        map.from(poolProperties::getHeartbeatInterval).asInt((v0) -> {
            return v0.toMillis();
        }).to(heartBeatInterval -> {
            options.add((DriverOption) DefaultDriverOption.HEARTBEAT_INTERVAL, heartBeatInterval.intValue());
        });
    }

    private void mapRequestOptions(CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Request requestProperties = this.properties.getRequest();
        Objects.requireNonNull(requestProperties);
        map.from(requestProperties::getTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(timeout -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_TIMEOUT, timeout.intValue());
        });
        Objects.requireNonNull(requestProperties);
        map.from(requestProperties::getConsistency).to(consistency -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_CONSISTENCY, (Enum<?>) consistency);
        });
        Objects.requireNonNull(requestProperties);
        map.from(requestProperties::getSerialConsistency).to(serialConsistency -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, (Enum<?>) serialConsistency);
        });
        Objects.requireNonNull(requestProperties);
        map.from(requestProperties::getPageSize).to(pageSize -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_PAGE_SIZE, pageSize.intValue());
        });
        CassandraProperties.Throttler throttlerProperties = requestProperties.getThrottler();
        Objects.requireNonNull(throttlerProperties);
        map.from(throttlerProperties::getType).as((v0) -> {
            return v0.type();
        }).to(type -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_CLASS, type);
        });
        Objects.requireNonNull(throttlerProperties);
        map.from(throttlerProperties::getMaxQueueSize).to(maxQueueSize -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_QUEUE_SIZE, maxQueueSize.intValue());
        });
        Objects.requireNonNull(throttlerProperties);
        map.from(throttlerProperties::getMaxConcurrentRequests).to(maxConcurrentRequests -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_CONCURRENT_REQUESTS, maxConcurrentRequests.intValue());
        });
        Objects.requireNonNull(throttlerProperties);
        map.from(throttlerProperties::getMaxRequestsPerSecond).to(maxRequestsPerSecond -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_REQUESTS_PER_SECOND, maxRequestsPerSecond.intValue());
        });
        Objects.requireNonNull(throttlerProperties);
        map.from(throttlerProperties::getDrainInterval).asInt((v0) -> {
            return v0.toMillis();
        }).to(drainInterval -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_DRAIN_INTERVAL, drainInterval.intValue());
        });
    }

    private void mapControlConnectionOptions(CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Controlconnection controlProperties = this.properties.getControlconnection();
        Objects.requireNonNull(controlProperties);
        map.from(controlProperties::getTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(timeout -> {
            options.add((DriverOption) DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT, timeout.intValue());
        });
    }

    private List<String> mapContactPoints(CassandraConnectionDetails connectionDetails) {
        return connectionDetails.getContactPoints().stream().map(node -> {
            return node.host() + ":" + node.port();
        }).toList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration$CassandraDriverOptions.class */
    public static final class CassandraDriverOptions {
        private final Map<String, String> options = new LinkedHashMap();

        private CassandraDriverOptions() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, String value) {
            String key = createKeyFor(option);
            this.options.put(key, value);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, int value) {
            return add(option, String.valueOf(value));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, Enum<?> value) {
            return add(option, value.name());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, List<String> values) {
            for (int i = 0; i < values.size(); i++) {
                this.options.put(String.format("%s.%s", createKeyFor(option), Integer.valueOf(i)), values.get(i));
            }
            return this;
        }

        private Config build() {
            return ConfigFactory.parseMap(this.options, "Environment");
        }

        private static String createKeyFor(DriverOption option) {
            return String.format("%s.%s", "datastax-java-driver", option.getPath());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration$PropertiesCassandraConnectionDetails.class */
    static final class PropertiesCassandraConnectionDetails implements CassandraConnectionDetails {
        private final CassandraProperties properties;

        private PropertiesCassandraConnectionDetails(CassandraProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails
        public List<CassandraConnectionDetails.Node> getContactPoints() {
            List<String> contactPoints = this.properties.getContactPoints();
            return contactPoints != null ? contactPoints.stream().map(this::asNode).toList() : Collections.emptyList();
        }

        @Override // org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails
        public String getUsername() {
            return this.properties.getUsername();
        }

        @Override // org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }

        @Override // org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails
        public String getLocalDatacenter() {
            return this.properties.getLocalDatacenter();
        }

        private CassandraConnectionDetails.Node asNode(String contactPoint) {
            int i = contactPoint.lastIndexOf(58);
            if (i >= 0) {
                String portCandidate = contactPoint.substring(i + 1);
                Integer port = asPort(portCandidate);
                if (port != null) {
                    return new CassandraConnectionDetails.Node(contactPoint.substring(0, i), port.intValue());
                }
            }
            return new CassandraConnectionDetails.Node(contactPoint, this.properties.getPort());
        }

        private Integer asPort(String value) {
            try {
                int i = Integer.parseInt(value);
                if (i <= 0 || i >= 65535) {
                    return null;
                }
                return Integer.valueOf(i);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
