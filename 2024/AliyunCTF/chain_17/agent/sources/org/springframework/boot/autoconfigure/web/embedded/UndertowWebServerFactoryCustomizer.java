package org.springframework.boot.autoconfigure.web.embedded;

import io.undertow.UndertowOptions;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.xnio.Option;
import org.xnio.Options;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer.class */
public class UndertowWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public UndertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableUndertowWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerOptions options = new ServerOptions(factory);
        ServerProperties properties = this.serverProperties;
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpRequestHeaderSize).asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(options.option(UndertowOptions.MAX_HEADER_SIZE));
        mapUndertowProperties(factory, options);
        mapAccessLogProperties(factory);
        PropertyMapper.Source from = map.from(this::getOrDeduceUseForwardHeaders);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setUseForwardHeaders(v1);
        });
    }

    private void mapUndertowProperties(ConfigurableUndertowWebServerFactory factory, ServerOptions serverOptions) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties.Undertow properties = this.serverProperties.getUndertow();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<Integer> asInt = map.from(properties::getBufferSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        });
        Objects.requireNonNull(factory);
        asInt.to(factory::setBufferSize);
        ServerProperties.Undertow.Threads threadProperties = properties.getThreads();
        Objects.requireNonNull(threadProperties);
        PropertyMapper.Source from = map.from(threadProperties::getIo);
        Objects.requireNonNull(factory);
        from.to(factory::setIoThreads);
        Objects.requireNonNull(threadProperties);
        PropertyMapper.Source from2 = map.from(threadProperties::getWorker);
        Objects.requireNonNull(factory);
        from2.to(factory::setWorkerThreads);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getDirectBuffers);
        Objects.requireNonNull(factory);
        from3.to(factory::setUseDirectBuffers);
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpPostSize).as((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(serverOptions.option(UndertowOptions.MAX_ENTITY_SIZE));
        Objects.requireNonNull(properties);
        map.from(properties::getMaxParameters).to(serverOptions.option(UndertowOptions.MAX_PARAMETERS));
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHeaders).to(serverOptions.option(UndertowOptions.MAX_HEADERS));
        Objects.requireNonNull(properties);
        map.from(properties::getMaxCookies).to(serverOptions.option(UndertowOptions.MAX_COOKIES));
        mapSlashProperties(properties, serverOptions);
        Objects.requireNonNull(properties);
        map.from(properties::isDecodeUrl).to(serverOptions.option(UndertowOptions.DECODE_URL));
        Objects.requireNonNull(properties);
        map.from(properties::getUrlCharset).as((v0) -> {
            return v0.name();
        }).to(serverOptions.option(UndertowOptions.URL_CHARSET));
        Objects.requireNonNull(properties);
        map.from(properties::isAlwaysSetKeepAlive).to(serverOptions.option(UndertowOptions.ALWAYS_SET_KEEP_ALIVE));
        Objects.requireNonNull(properties);
        map.from(properties::getNoRequestTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(serverOptions.option(UndertowOptions.NO_REQUEST_TIMEOUT));
        ServerProperties.Undertow.Options options = properties.getOptions();
        Objects.requireNonNull(options);
        PropertyMapper.Source from4 = map.from(options::getServer);
        Objects.requireNonNull(serverOptions);
        from4.to(serverOptions.forEach(serverOptions::option));
        SocketOptions socketOptions = new SocketOptions(factory);
        ServerProperties.Undertow.Options options2 = properties.getOptions();
        Objects.requireNonNull(options2);
        PropertyMapper.Source from5 = map.from(options2::getSocket);
        Objects.requireNonNull(socketOptions);
        from5.to(socketOptions.forEach(socketOptions::option));
    }

    private void mapSlashProperties(ServerProperties.Undertow properties, ServerOptions serverOptions) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        map.from(properties::isAllowEncodedSlash).to(serverOptions.option(UndertowOptions.ALLOW_ENCODED_SLASH));
        Objects.requireNonNull(properties);
        map.from(properties::getDecodeSlash).to(serverOptions.option(UndertowOptions.DECODE_SLASH));
    }

    private boolean isPositive(Number value) {
        return value.longValue() > 0;
    }

    private void mapAccessLogProperties(ConfigurableUndertowWebServerFactory factory) {
        ServerProperties.Undertow.Accesslog properties = this.serverProperties.getUndertow().getAccesslog();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::isEnabled);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setAccessLogEnabled(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getDir);
        Objects.requireNonNull(factory);
        from2.to(factory::setAccessLogDirectory);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getPattern);
        Objects.requireNonNull(factory);
        from3.to(factory::setAccessLogPattern);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getPrefix);
        Objects.requireNonNull(factory);
        from4.to(factory::setAccessLogPrefix);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from5 = map.from(properties::getSuffix);
        Objects.requireNonNull(factory);
        from5.to(factory::setAccessLogSuffix);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from6 = map.from(properties::isRotate);
        Objects.requireNonNull(factory);
        from6.to((v1) -> {
            r1.setAccessLogRotate(v1);
        });
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$AbstractOptions.class */
    public static abstract class AbstractOptions {
        private final Class<?> source;
        private final Map<String, Option<?>> nameLookup;
        private final ConfigurableUndertowWebServerFactory factory;

        AbstractOptions(Class<?> source, ConfigurableUndertowWebServerFactory factory) {
            Map<String, Option<?>> lookup = new HashMap<>();
            ReflectionUtils.doWithLocalFields(source, field -> {
                int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Option.class.isAssignableFrom(field.getType())) {
                    try {
                        Option<?> option = (Option) field.get(null);
                        lookup.put(getCanonicalName(field.getName()), option);
                    } catch (IllegalAccessException e) {
                    }
                }
            });
            this.source = source;
            this.nameLookup = Collections.unmodifiableMap(lookup);
            this.factory = factory;
        }

        protected ConfigurableUndertowWebServerFactory getFactory() {
            return this.factory;
        }

        <T> Consumer<Map<String, String>> forEach(Function<Option<T>, Consumer<T>> function) {
            return map -> {
                map.forEach((key, value) -> {
                    Option<?> option = this.nameLookup.get(getCanonicalName(key));
                    Assert.state(option != null, (Supplier<String>) () -> {
                        return "Unable to find '" + key + "' in " + ClassUtils.getShortName(this.source);
                    });
                    ((Consumer) function.apply(option)).accept(option.parseValue(value, getClass().getClassLoader()));
                });
            };
        }

        private static String getCanonicalName(String name) {
            StringBuilder canonicalName = new StringBuilder(name.length());
            name.chars().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach(c -> {
                canonicalName.append((char) c);
            });
            return canonicalName.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$ServerOptions.class */
    public static class ServerOptions extends AbstractOptions {
        ServerOptions(ConfigurableUndertowWebServerFactory factory) {
            super(UndertowOptions.class, factory);
        }

        <T> Consumer<T> option(Option<T> option) {
            return value -> {
                getFactory().addBuilderCustomizers(builder -> {
                    builder.setServerOption(option, value);
                });
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer$SocketOptions.class */
    public static class SocketOptions extends AbstractOptions {
        SocketOptions(ConfigurableUndertowWebServerFactory factory) {
            super(Options.class, factory);
        }

        <T> Consumer<T> option(Option<T> option) {
            return value -> {
                getFactory().addBuilderCustomizers(builder -> {
                    builder.setSocketOption(option, value);
                });
            };
        }
    }
}
