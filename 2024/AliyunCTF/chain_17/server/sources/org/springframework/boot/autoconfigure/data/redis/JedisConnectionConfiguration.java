package org.springframework.boot.autoconfigure.data.redis;

import java.util.Objects;
import javax.net.ssl.SSLParameters;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
@ConditionalOnMissingBean({RedisConnectionFactory.class})
@ConditionalOnProperty(name = {"spring.data.redis.client-type"}, havingValue = "jedis", matchIfMissing = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/JedisConnectionConfiguration.class */
class JedisConnectionConfiguration extends RedisConnectionConfiguration {
    JedisConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration, ObjectProvider<RedisClusterConfiguration> clusterConfiguration, RedisConnectionDetails connectionDetails, ObjectProvider<SslBundles> sslBundles) {
        super(properties, connectionDetails, standaloneConfigurationProvider, sentinelConfiguration, clusterConfiguration, sslBundles);
    }

    @ConditionalOnThreading(Threading.PLATFORM)
    @Bean
    JedisConnectionFactory redisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        return createJedisConnectionFactory(builderCustomizers);
    }

    @ConditionalOnThreading(Threading.VIRTUAL)
    @Bean
    JedisConnectionFactory redisConnectionFactoryVirtualThreads(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisConnectionFactory factory = createJedisConnectionFactory(builderCustomizers);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        factory.setExecutor(executor);
        return factory;
    }

    private JedisConnectionFactory createJedisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers);
        if (getSentinelConfig() != null) {
            return new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
        }
        return new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        if (isSslEnabled()) {
            applySsl(builder);
        }
        RedisProperties.Pool pool = getProperties().getJedis().getPool();
        if (isPoolEnabled(pool)) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) getProperties().getTimeout());
        Objects.requireNonNull(builder);
        from.to(builder::readTimeout);
        PropertyMapper.Source from2 = map.from((PropertyMapper) getProperties().getConnectTimeout());
        Objects.requireNonNull(builder);
        from2.to(builder::connectTimeout);
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) getProperties().getClientName()).whenHasText();
        Objects.requireNonNull(builder);
        whenHasText.to(builder::clientName);
        return builder;
    }

    private void applySsl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        JedisClientConfiguration.JedisSslClientConfigurationBuilder sslBuilder = builder.useSsl();
        if (getProperties().getSsl().getBundle() != null) {
            SslBundle sslBundle = getSslBundles().getBundle(getProperties().getSsl().getBundle());
            sslBuilder.sslSocketFactory(sslBundle.createSslContext().getSocketFactory());
            SslOptions sslOptions = sslBundle.getOptions();
            SSLParameters sslParameters = new SSLParameters();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) sslOptions.getCiphers());
            Objects.requireNonNull(sslParameters);
            from.to(sslParameters::setCipherSuites);
            PropertyMapper.Source from2 = map.from((PropertyMapper) sslOptions.getEnabledProtocols());
            Objects.requireNonNull(sslParameters);
            from2.to(sslParameters::setProtocols);
            sslBuilder.sslParameters(sslParameters);
        }
    }

    private void applyPooling(RedisProperties.Pool pool, JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRuns(pool.getTimeBetweenEvictionRuns());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWait(pool.getMaxWait());
        }
        return config;
    }

    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        if (urlUsesSsl()) {
            builder.useSsl();
        }
    }
}
