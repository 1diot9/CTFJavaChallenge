package org.springframework.boot.r2dbc;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.PoolingConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/ConnectionFactoryBuilder.class */
public final class ConnectionFactoryBuilder {
    private static final OptionsCapableWrapper optionsCapableWrapper;
    private static final String COLON = ":";
    private final ConnectionFactoryOptions.Builder optionsBuilder;
    private final List<ConnectionFactoryDecorator> decorators = new ArrayList();

    static {
        if (ClassUtils.isPresent("io.r2dbc.pool.ConnectionPool", ConnectionFactoryBuilder.class.getClassLoader())) {
            optionsCapableWrapper = new PoolingAwareOptionsCapableWrapper();
        } else {
            optionsCapableWrapper = new OptionsCapableWrapper();
        }
    }

    private ConnectionFactoryBuilder(ConnectionFactoryOptions.Builder optionsBuilder) {
        this.optionsBuilder = optionsBuilder;
    }

    public static ConnectionFactoryBuilder withUrl(String url) {
        Assert.hasText(url, (Supplier<String>) () -> {
            return "Url must not be null";
        });
        return withOptions(ConnectionFactoryOptions.parse(url).mutate());
    }

    public static ConnectionFactoryBuilder withOptions(ConnectionFactoryOptions.Builder options) {
        return new ConnectionFactoryBuilder(options);
    }

    public static ConnectionFactoryBuilder derivedFrom(ConnectionFactory connectionFactory) {
        ConnectionFactoryOptions options = extractOptionsIfPossible(connectionFactory);
        if (options == null) {
            throw new IllegalArgumentException("ConnectionFactoryOptions could not be extracted from " + connectionFactory);
        }
        return withOptions(options.mutate());
    }

    private static ConnectionFactoryOptions extractOptionsIfPossible(ConnectionFactory connectionFactory) {
        OptionsCapableConnectionFactory optionsCapable = OptionsCapableConnectionFactory.unwrapFrom(connectionFactory);
        if (optionsCapable != null) {
            return optionsCapable.getOptions();
        }
        return null;
    }

    public ConnectionFactoryBuilder configure(Consumer<ConnectionFactoryOptions.Builder> options) {
        options.accept(this.optionsBuilder);
        return this;
    }

    public ConnectionFactoryBuilder username(String username) {
        return configure(options -> {
            options.option(ConnectionFactoryOptions.USER, username);
        });
    }

    public ConnectionFactoryBuilder password(CharSequence password) {
        return configure(options -> {
            options.option(ConnectionFactoryOptions.PASSWORD, password);
        });
    }

    public ConnectionFactoryBuilder hostname(String host) {
        return configure(options -> {
            options.option(ConnectionFactoryOptions.HOST, host);
        });
    }

    public ConnectionFactoryBuilder port(int port) {
        return configure(options -> {
            options.option(ConnectionFactoryOptions.PORT, Integer.valueOf(port));
        });
    }

    public ConnectionFactoryBuilder database(String database) {
        return configure(options -> {
            options.option(ConnectionFactoryOptions.DATABASE, database);
        });
    }

    public ConnectionFactoryBuilder decorator(ConnectionFactoryDecorator decorator) {
        this.decorators.add(decorator);
        return this;
    }

    public ConnectionFactoryBuilder decorators(Iterable<ConnectionFactoryDecorator> decorators) {
        for (ConnectionFactoryDecorator decorator : decorators) {
            this.decorators.add(decorator);
        }
        return this;
    }

    public ConnectionFactory build() {
        ConnectionFactoryOptions options = buildOptions();
        ConnectionFactory connectionFactory = optionsCapableWrapper.buildAndWrap(options);
        for (ConnectionFactoryDecorator decorator : this.decorators) {
            connectionFactory = decorator.decorate(connectionFactory);
        }
        return connectionFactory;
    }

    public ConnectionFactoryOptions buildOptions() {
        return this.optionsBuilder.build();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/ConnectionFactoryBuilder$OptionsCapableWrapper.class */
    private static class OptionsCapableWrapper {
        private OptionsCapableWrapper() {
        }

        ConnectionFactory buildAndWrap(ConnectionFactoryOptions options) {
            ConnectionFactory connectionFactory = ConnectionFactories.get(options);
            return new OptionsCapableConnectionFactory(options, connectionFactory);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/ConnectionFactoryBuilder$PoolingAwareOptionsCapableWrapper.class */
    static final class PoolingAwareOptionsCapableWrapper extends OptionsCapableWrapper {
        private final PoolingConnectionFactoryProvider poolingProvider = new PoolingConnectionFactoryProvider();

        PoolingAwareOptionsCapableWrapper() {
        }

        @Override // org.springframework.boot.r2dbc.ConnectionFactoryBuilder.OptionsCapableWrapper
        ConnectionFactory buildAndWrap(ConnectionFactoryOptions options) {
            if (!this.poolingProvider.supports(options)) {
                return super.buildAndWrap(options);
            }
            ConnectionFactoryOptions delegateOptions = delegateFactoryOptions(options);
            ConnectionFactory connectionFactory = super.buildAndWrap(delegateOptions);
            ConnectionPoolConfiguration poolConfiguration = connectionPoolConfiguration(delegateOptions, connectionFactory);
            return new ConnectionPool(poolConfiguration);
        }

        private ConnectionFactoryOptions delegateFactoryOptions(ConnectionFactoryOptions options) {
            String protocol = toString(options.getRequiredValue(ConnectionFactoryOptions.PROTOCOL));
            if (protocol.trim().isEmpty()) {
                throw new IllegalArgumentException(String.format("Protocol %s is not valid.", protocol));
            }
            String[] protocols = protocol.split(":", 2);
            String driverDelegate = protocols[0];
            String protocolDelegate = protocols.length != 2 ? "" : protocols[1];
            return ConnectionFactoryOptions.builder().from(options).option(ConnectionFactoryOptions.DRIVER, driverDelegate).option(ConnectionFactoryOptions.PROTOCOL, protocolDelegate).build();
        }

        ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionFactoryOptions options, ConnectionFactory connectionFactory) {
            ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source as = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.BACKGROUND_EVICTION_INTERVAL)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as.to(builder::backgroundEvictionInterval);
            PropertyMapper.Source as2 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.INITIAL_SIZE)).as(this::toInteger);
            Objects.requireNonNull(builder);
            as2.to((v1) -> {
                r1.initialSize(v1);
            });
            PropertyMapper.Source as3 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_SIZE)).as(this::toInteger);
            Objects.requireNonNull(builder);
            as3.to((v1) -> {
                r1.maxSize(v1);
            });
            PropertyMapper.Source as4 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.ACQUIRE_RETRY)).as(this::toInteger);
            Objects.requireNonNull(builder);
            as4.to((v1) -> {
                r1.acquireRetry(v1);
            });
            PropertyMapper.Source as5 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_LIFE_TIME)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as5.to(builder::maxLifeTime);
            PropertyMapper.Source as6 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_ACQUIRE_TIME)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as6.to(builder::maxAcquireTime);
            PropertyMapper.Source as7 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_IDLE_TIME)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as7.to(builder::maxIdleTime);
            PropertyMapper.Source as8 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_CREATE_CONNECTION_TIME)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as8.to(builder::maxCreateConnectionTime);
            PropertyMapper.Source as9 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MAX_VALIDATION_TIME)).as(this::toDuration);
            Objects.requireNonNull(builder);
            as9.to(builder::maxValidationTime);
            PropertyMapper.Source as10 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.MIN_IDLE)).as(this::toInteger);
            Objects.requireNonNull(builder);
            as10.to((v1) -> {
                r1.minIdle(v1);
            });
            PropertyMapper.Source as11 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.POOL_NAME)).as(this::toString);
            Objects.requireNonNull(builder);
            as11.to(builder::name);
            map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.PRE_RELEASE)).to(function -> {
                builder.preRelease((Function) function);
            });
            map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.POST_ALLOCATE)).to(function2 -> {
                builder.postAllocate((Function) function2);
            });
            PropertyMapper.Source as12 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.REGISTER_JMX)).as(this::toBoolean);
            Objects.requireNonNull(builder);
            as12.to((v1) -> {
                r1.registerJmx(v1);
            });
            PropertyMapper.Source as13 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.VALIDATION_QUERY)).as(this::toString);
            Objects.requireNonNull(builder);
            as13.to(builder::validationQuery);
            PropertyMapper.Source as14 = map.from((PropertyMapper) options.getValue(PoolingConnectionFactoryProvider.VALIDATION_DEPTH)).as(this::toValidationDepth);
            Objects.requireNonNull(builder);
            as14.to(builder::validationDepth);
            return builder.build();
        }

        private String toString(Object object) {
            return (String) toType(String.class, object, (v0) -> {
                return String.valueOf(v0);
            });
        }

        private Integer toInteger(Object object) {
            return (Integer) toType(Integer.class, object, Integer::valueOf);
        }

        private Duration toDuration(Object object) {
            return (Duration) toType(Duration.class, object, (v0) -> {
                return Duration.parse(v0);
            });
        }

        private Boolean toBoolean(Object object) {
            return (Boolean) toType(Boolean.class, object, Boolean::valueOf);
        }

        private ValidationDepth toValidationDepth(Object object) {
            return (ValidationDepth) toType(ValidationDepth.class, object, string -> {
                return ValidationDepth.valueOf(string.toUpperCase(Locale.ENGLISH));
            });
        }

        private <T> T toType(Class<T> type, Object object, Function<String, T> converter) {
            if (type.isInstance(object)) {
                return type.cast(object);
            }
            if (object instanceof String) {
                String string = (String) object;
                return converter.apply(string);
            }
            throw new IllegalArgumentException("Cannot convert '" + object + "' to " + type.getName());
        }
    }
}
