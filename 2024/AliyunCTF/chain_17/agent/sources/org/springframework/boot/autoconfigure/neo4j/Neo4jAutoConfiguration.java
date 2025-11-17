package org.springframework.boot.autoconfigure.neo4j;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokenManager;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.internal.Scheme;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({Neo4jProperties.class})
@AutoConfiguration
@ConditionalOnClass({Driver.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jAutoConfiguration.class */
public class Neo4jAutoConfiguration {
    @ConditionalOnMissingBean({Neo4jConnectionDetails.class})
    @Bean
    PropertiesNeo4jConnectionDetails neo4jConnectionDetails(Neo4jProperties properties, ObjectProvider<AuthTokenManager> authTokenManager) {
        return new PropertiesNeo4jConnectionDetails(properties, authTokenManager.getIfUnique());
    }

    @ConditionalOnMissingBean
    @Bean
    public Driver neo4jDriver(Neo4jProperties properties, Environment environment, Neo4jConnectionDetails connectionDetails, ObjectProvider<ConfigBuilderCustomizer> configBuilderCustomizers) {
        Config config = mapDriverConfig(properties, connectionDetails, configBuilderCustomizers.orderedStream().toList());
        AuthTokenManager authTokenManager = connectionDetails.getAuthTokenManager();
        if (authTokenManager != null) {
            return GraphDatabase.driver(connectionDetails.getUri(), authTokenManager, config);
        }
        AuthToken authToken = connectionDetails.getAuthToken();
        return GraphDatabase.driver(connectionDetails.getUri(), authToken, config);
    }

    Config mapDriverConfig(Neo4jProperties properties, Neo4jConnectionDetails connectionDetails, List<ConfigBuilderCustomizer> customizers) {
        Config.ConfigBuilder builder = Config.builder();
        configurePoolSettings(builder, properties.getPool());
        URI uri = connectionDetails.getUri();
        String scheme = uri != null ? uri.getScheme() : "bolt";
        configureDriverSettings(builder, properties, isSimpleScheme(scheme));
        builder.withLogging(new Neo4jSpringJclLogging());
        customizers.forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private boolean isSimpleScheme(String scheme) {
        String lowerCaseScheme = scheme.toLowerCase(Locale.ENGLISH);
        try {
            Scheme.validateScheme(lowerCaseScheme);
            return lowerCaseScheme.equals("bolt") || lowerCaseScheme.equals("neo4j");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("'%s' is not a supported scheme.", scheme));
        }
    }

    private void configurePoolSettings(Config.ConfigBuilder builder, Neo4jProperties.Pool pool) {
        if (pool.isLogLeakedSessions()) {
            builder.withLeakedSessionsLogging();
        }
        builder.withMaxConnectionPoolSize(pool.getMaxConnectionPoolSize());
        Duration idleTimeBeforeConnectionTest = pool.getIdleTimeBeforeConnectionTest();
        if (idleTimeBeforeConnectionTest != null) {
            builder.withConnectionLivenessCheckTimeout(idleTimeBeforeConnectionTest.toMillis(), TimeUnit.MILLISECONDS);
        }
        builder.withMaxConnectionLifetime(pool.getMaxConnectionLifetime().toMillis(), TimeUnit.MILLISECONDS);
        builder.withConnectionAcquisitionTimeout(pool.getConnectionAcquisitionTimeout().toMillis(), TimeUnit.MILLISECONDS);
        if (pool.isMetricsEnabled()) {
            builder.withDriverMetrics();
        } else {
            builder.withoutDriverMetrics();
        }
    }

    private void configureDriverSettings(Config.ConfigBuilder builder, Neo4jProperties properties, boolean withEncryptionAndTrustSettings) {
        if (withEncryptionAndTrustSettings) {
            applyEncryptionAndTrustSettings(builder, properties.getSecurity());
        }
        builder.withConnectionTimeout(properties.getConnectionTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.withMaxTransactionRetryTime(properties.getMaxTransactionRetryTime().toMillis(), TimeUnit.MILLISECONDS);
    }

    private void applyEncryptionAndTrustSettings(Config.ConfigBuilder builder, Neo4jProperties.Security securityProperties) {
        if (securityProperties.isEncrypted()) {
            builder.withEncryption();
        } else {
            builder.withoutEncryption();
        }
        builder.withTrustStrategy(mapTrustStrategy(securityProperties));
    }

    private Config.TrustStrategy mapTrustStrategy(Neo4jProperties.Security securityProperties) {
        Neo4jProperties.Security.TrustStrategy strategy = securityProperties.getTrustStrategy();
        Config.TrustStrategy trustStrategy = createTrustStrategy(securityProperties, "spring.neo4j.security.trust-strategy", strategy);
        if (securityProperties.isHostnameVerificationEnabled()) {
            trustStrategy.withHostnameVerification();
        } else {
            trustStrategy.withoutHostnameVerification();
        }
        return trustStrategy;
    }

    private Config.TrustStrategy createTrustStrategy(Neo4jProperties.Security securityProperties, String propertyName, Neo4jProperties.Security.TrustStrategy strategy) {
        switch (strategy) {
            case TRUST_ALL_CERTIFICATES:
                return Config.TrustStrategy.trustAllCertificates();
            case TRUST_SYSTEM_CA_SIGNED_CERTIFICATES:
                return Config.TrustStrategy.trustSystemCertificates();
            case TRUST_CUSTOM_CA_SIGNED_CERTIFICATES:
                File certFile = securityProperties.getCertFile();
                if (certFile == null || !certFile.isFile()) {
                    throw new InvalidConfigurationPropertyValueException(propertyName, strategy.name(), "Configured trust strategy requires a certificate file.");
                }
                return Config.TrustStrategy.trustCustomCertificateSignedBy(new File[]{certFile});
            default:
                throw new InvalidConfigurationPropertyValueException(propertyName, strategy.name(), "Unknown strategy.");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jAutoConfiguration$PropertiesNeo4jConnectionDetails.class */
    static class PropertiesNeo4jConnectionDetails implements Neo4jConnectionDetails {
        private final Neo4jProperties properties;
        private final AuthTokenManager authTokenManager;

        PropertiesNeo4jConnectionDetails(Neo4jProperties properties, AuthTokenManager authTokenManager) {
            this.properties = properties;
            this.authTokenManager = authTokenManager;
        }

        @Override // org.springframework.boot.autoconfigure.neo4j.Neo4jConnectionDetails
        public URI getUri() {
            URI uri = this.properties.getUri();
            return uri != null ? uri : super.getUri();
        }

        @Override // org.springframework.boot.autoconfigure.neo4j.Neo4jConnectionDetails
        public AuthToken getAuthToken() {
            Neo4jProperties.Authentication authentication = this.properties.getAuthentication();
            String username = authentication.getUsername();
            String kerberosTicket = authentication.getKerberosTicket();
            boolean hasUsername = StringUtils.hasText(username);
            boolean hasKerberosTicket = StringUtils.hasText(kerberosTicket);
            Assert.state((hasUsername && hasKerberosTicket) ? false : true, (Supplier<String>) () -> {
                return "Cannot specify both username ('%s') and kerberos ticket ('%s')".formatted(username, kerberosTicket);
            });
            String password = authentication.getPassword();
            if (hasUsername && StringUtils.hasText(password)) {
                return AuthTokens.basic(username, password, authentication.getRealm());
            }
            if (hasKerberosTicket) {
                return AuthTokens.kerberos(kerberosTicket);
            }
            return AuthTokens.none();
        }

        @Override // org.springframework.boot.autoconfigure.neo4j.Neo4jConnectionDetails
        public AuthTokenManager getAuthTokenManager() {
            return this.authTokenManager;
        }
    }
}
