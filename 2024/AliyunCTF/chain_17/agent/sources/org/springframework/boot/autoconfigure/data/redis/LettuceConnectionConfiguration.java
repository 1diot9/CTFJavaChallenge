package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisClient.class})
@ConditionalOnProperty(name = {"spring.data.redis.client-type"}, havingValue = "lettuce", matchIfMissing = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/LettuceConnectionConfiguration.class */
class LettuceConnectionConfiguration extends RedisConnectionConfiguration {
    LettuceConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, RedisConnectionDetails connectionDetails, ObjectProvider<SslBundles> sslBundles) {
        super(properties, connectionDetails, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider, sslBundles);
    }

    @ConditionalOnMissingBean({ClientResources.class})
    @Bean(destroyMethod = "shutdown")
    DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
        DefaultClientResources.Builder builder = DefaultClientResources.builder();
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    @ConditionalOnThreading(Threading.PLATFORM)
    @Bean
    LettuceConnectionFactory redisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        return createConnectionFactory(builderCustomizers, clientResources);
    }

    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    @ConditionalOnThreading(Threading.VIRTUAL)
    @Bean
    LettuceConnectionFactory redisConnectionFactoryVirtualThreads(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        LettuceConnectionFactory factory = createConnectionFactory(builderCustomizers, clientResources);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        factory.setExecutor(executor);
        return factory;
    }

    private LettuceConnectionFactory createConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
        return createLettuceConnectionFactory(clientConfig);
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
        if (getSentinelConfig() != null) {
            return new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new LettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisProperties.Pool pool) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(createClientOptions());
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (isPoolEnabled(pool)) {
            return new PoolBuilderFactory().createBuilder(pool);
        }
        return LettuceClientConfiguration.builder();
    }

    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (isSslEnabled()) {
            builder.useSsl();
        }
        if (getProperties().getTimeout() != null) {
            builder.commandTimeout(getProperties().getTimeout());
        }
        if (getProperties().getLettuce() != null) {
            RedisProperties.Lettuce lettuce = getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
    }

    private ClientOptions createClientOptions() {
        ClientOptions.Builder builder = initializeClientOptionsBuilder();
        Duration connectTimeout = getProperties().getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        if (isSslEnabled() && getProperties().getSsl().getBundle() != null) {
            SslBundle sslBundle = getSslBundles().getBundle(getProperties().getSsl().getBundle());
            SslOptions.Builder sslOptionsBuilder = SslOptions.builder();
            sslOptionsBuilder.keyManager(sslBundle.getManagers().getKeyManagerFactory());
            sslOptionsBuilder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            org.springframework.boot.ssl.SslOptions sslOptions = sslBundle.getOptions();
            if (sslOptions.getCiphers() != null) {
                sslOptionsBuilder.cipherSuites(sslOptions.getCiphers());
            }
            if (sslOptions.getEnabledProtocols() != null) {
                sslOptionsBuilder.protocols(sslOptions.getEnabledProtocols());
            }
            builder.sslOptions(sslOptionsBuilder.build());
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder().dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }
            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }
            return builder.topologyRefreshOptions(refreshBuilder.build());
        }
        return ClientOptions.builder();
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (urlUsesSsl()) {
            builder.useSsl();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/LettuceConnectionConfiguration$PoolBuilderFactory.class */
    public static final class PoolBuilderFactory {
        private PoolBuilderFactory() {
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }
            return config;
        }
    }
}
