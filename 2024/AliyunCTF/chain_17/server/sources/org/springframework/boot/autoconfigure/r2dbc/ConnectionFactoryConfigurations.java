package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.r2dbc.ConnectionFactoryDecorator;
import org.springframework.boot.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryConfigurations.class */
abstract class ConnectionFactoryConfigurations {
    ConnectionFactoryConfigurations() {
    }

    protected static ConnectionFactory createConnectionFactory(R2dbcProperties properties, R2dbcConnectionDetails connectionDetails, ClassLoader classLoader, List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers, List<ConnectionFactoryDecorator> decorators) {
        try {
            return ConnectionFactoryBuilder.withOptions(new ConnectionFactoryOptionsInitializer().initialize(properties, connectionDetails, () -> {
                return EmbeddedDatabaseConnection.get(classLoader);
            })).configure(options -> {
                Iterator it = optionsCustomizers.iterator();
                while (it.hasNext()) {
                    ConnectionFactoryOptionsBuilderCustomizer optionsCustomizer = (ConnectionFactoryOptionsBuilderCustomizer) it.next();
                    optionsCustomizer.customize(options);
                }
            }).decorators(decorators).build();
        } catch (IllegalStateException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("driver=pool") && !ClassUtils.isPresent("io.r2dbc.pool.ConnectionPool", classLoader)) {
                throw new MissingR2dbcPoolDependencyException();
            }
            throw ex;
        }
    }

    @ConditionalOnMissingBean({ConnectionFactory.class})
    @Configuration(proxyBeanMethods = false)
    @Conditional({PooledConnectionFactoryCondition.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryConfigurations$PoolConfiguration.class */
    static class PoolConfiguration {
        PoolConfiguration() {
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({ConnectionPool.class})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryConfigurations$PoolConfiguration$PooledConnectionFactoryConfiguration.class */
        static class PooledConnectionFactoryConfiguration {
            PooledConnectionFactoryConfiguration() {
            }

            @Bean(destroyMethod = "dispose")
            ConnectionPool connectionFactory(R2dbcProperties properties, ObjectProvider<R2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader, ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers, ObjectProvider<ConnectionFactoryDecorator> decorators) {
                ConnectionFactory connectionFactory = ConnectionFactoryConfigurations.createConnectionFactory(properties, connectionDetails.getIfAvailable(), resourceLoader.getClassLoader(), customizers.orderedStream().toList(), decorators.orderedStream().toList());
                R2dbcProperties.Pool pool = properties.getPool();
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
                PropertyMapper.Source from = map.from((PropertyMapper) pool.getMaxIdleTime());
                Objects.requireNonNull(builder);
                from.to(builder::maxIdleTime);
                PropertyMapper.Source from2 = map.from((PropertyMapper) pool.getMaxLifeTime());
                Objects.requireNonNull(builder);
                from2.to(builder::maxLifeTime);
                PropertyMapper.Source from3 = map.from((PropertyMapper) pool.getMaxAcquireTime());
                Objects.requireNonNull(builder);
                from3.to(builder::maxAcquireTime);
                PropertyMapper.Source from4 = map.from((PropertyMapper) pool.getMaxCreateConnectionTime());
                Objects.requireNonNull(builder);
                from4.to(builder::maxCreateConnectionTime);
                PropertyMapper.Source from5 = map.from((PropertyMapper) Integer.valueOf(pool.getInitialSize()));
                Objects.requireNonNull(builder);
                from5.to((v1) -> {
                    r1.initialSize(v1);
                });
                PropertyMapper.Source from6 = map.from((PropertyMapper) Integer.valueOf(pool.getMaxSize()));
                Objects.requireNonNull(builder);
                from6.to((v1) -> {
                    r1.maxSize(v1);
                });
                PropertyMapper.Source whenHasText = map.from((PropertyMapper) pool.getValidationQuery()).whenHasText();
                Objects.requireNonNull(builder);
                whenHasText.to(builder::validationQuery);
                PropertyMapper.Source from7 = map.from((PropertyMapper) pool.getValidationDepth());
                Objects.requireNonNull(builder);
                from7.to(builder::validationDepth);
                PropertyMapper.Source from8 = map.from((PropertyMapper) Integer.valueOf(pool.getMinIdle()));
                Objects.requireNonNull(builder);
                from8.to((v1) -> {
                    r1.minIdle(v1);
                });
                PropertyMapper.Source from9 = map.from((PropertyMapper) pool.getMaxValidationTime());
                Objects.requireNonNull(builder);
                from9.to(builder::maxValidationTime);
                return new ConnectionPool(builder.build());
            }
        }
    }

    @ConditionalOnMissingBean({ConnectionFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.r2dbc.pool", value = {"enabled"}, havingValue = "false", matchIfMissing = true)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryConfigurations$GenericConfiguration.class */
    static class GenericConfiguration {
        GenericConfiguration() {
        }

        @Bean
        ConnectionFactory connectionFactory(R2dbcProperties properties, ObjectProvider<R2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader, ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers, ObjectProvider<ConnectionFactoryDecorator> decorators) {
            return ConnectionFactoryConfigurations.createConnectionFactory(properties, connectionDetails.getIfAvailable(), resourceLoader.getClassLoader(), customizers.orderedStream().toList(), decorators.orderedStream().toList());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryConfigurations$PooledConnectionFactoryCondition.class */
    static class PooledConnectionFactoryCondition extends SpringBootCondition {
        PooledConnectionFactoryCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            BindResult<R2dbcProperties.Pool> pool = Binder.get(context.getEnvironment()).bind("spring.r2dbc.pool", Bindable.of(R2dbcProperties.Pool.class));
            if (hasPoolUrl(context.getEnvironment())) {
                if (pool.isBound()) {
                    throw new MultipleConnectionPoolConfigurationsException();
                }
                return ConditionOutcome.noMatch("URL-based pooling has been configured");
            }
            if (pool.isBound() && !ClassUtils.isPresent("io.r2dbc.pool.ConnectionPool", context.getClassLoader())) {
                throw new MissingR2dbcPoolDependencyException();
            }
            if (pool.orElseGet(R2dbcProperties.Pool::new).isEnabled()) {
                return ConditionOutcome.match("Property-based pooling is enabled");
            }
            return ConditionOutcome.noMatch("Property-based pooling is disabled");
        }

        private boolean hasPoolUrl(Environment environment) {
            String url = environment.getProperty("spring.r2dbc.url");
            return StringUtils.hasText(url) && url.contains(":pool:");
        }
    }
}
