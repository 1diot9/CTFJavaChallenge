package org.springframework.boot.logging;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggingSystemProperties.class */
public class LoggingSystemProperties {

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String PID_KEY = LoggingSystemProperty.PID.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String EXCEPTION_CONVERSION_WORD = LoggingSystemProperty.EXCEPTION_CONVERSION_WORD.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String LOG_FILE = LoggingSystemProperty.LOG_FILE.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String LOG_PATH = LoggingSystemProperty.LOG_PATH.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String CONSOLE_LOG_PATTERN = LoggingSystemProperty.CONSOLE_PATTERN.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String CONSOLE_LOG_CHARSET = LoggingSystemProperty.CONSOLE_CHARSET.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String CONSOLE_LOG_THRESHOLD = LoggingSystemProperty.CONSOLE_THRESHOLD.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String FILE_LOG_PATTERN = LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String FILE_LOG_CHARSET = LoggingSystemProperty.FILE_CHARSET.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String FILE_LOG_THRESHOLD = LoggingSystemProperty.FILE_THRESHOLD.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String LOG_LEVEL_PATTERN = LoggingSystemProperty.LEVEL_PATTERN.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String LOG_DATEFORMAT_PATTERN = LoggingSystemProperty.DATEFORMAT_PATTERN.getEnvironmentVariableName();
    private static final BiConsumer<String, String> systemPropertySetter = (name, value) -> {
        if (System.getProperty(name) == null && value != null) {
            System.setProperty(name, value);
        }
    };
    private final Environment environment;
    private final Function<String, String> defaultValueResolver;
    private final BiConsumer<String, String> setter;

    public LoggingSystemProperties(Environment environment) {
        this(environment, null);
    }

    public LoggingSystemProperties(Environment environment, BiConsumer<String, String> setter) {
        this(environment, null, setter);
    }

    public LoggingSystemProperties(Environment environment, Function<String, String> defaultValueResolver, BiConsumer<String, String> setter) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.defaultValueResolver = defaultValueResolver != null ? defaultValueResolver : name -> {
            return null;
        };
        this.setter = setter != null ? setter : systemPropertySetter;
    }

    protected Charset getDefaultCharset() {
        return StandardCharsets.UTF_8;
    }

    public final void apply() {
        apply(null);
    }

    public final void apply(LogFile logFile) {
        PropertyResolver resolver = getPropertyResolver();
        apply(logFile, resolver);
    }

    private PropertyResolver getPropertyResolver() {
        Environment environment = this.environment;
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
            PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(configurableEnvironment.getPropertySources());
            resolver.setConversionService(((ConfigurableEnvironment) this.environment).getConversionService());
            resolver.setIgnoreUnresolvableNestedPlaceholders(true);
            return resolver;
        }
        return this.environment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void apply(LogFile logFile, PropertyResolver resolver) {
        String defaultCharsetName = getDefaultCharset().name();
        setApplicationNameSystemProperty(resolver);
        setSystemProperty(LoggingSystemProperty.PID, new ApplicationPid().toString());
        setSystemProperty(LoggingSystemProperty.CONSOLE_CHARSET, resolver, defaultCharsetName);
        setSystemProperty(LoggingSystemProperty.FILE_CHARSET, resolver, defaultCharsetName);
        setSystemProperty(LoggingSystemProperty.CONSOLE_THRESHOLD, resolver);
        setSystemProperty(LoggingSystemProperty.FILE_THRESHOLD, resolver);
        setSystemProperty(LoggingSystemProperty.EXCEPTION_CONVERSION_WORD, resolver);
        setSystemProperty(LoggingSystemProperty.CONSOLE_PATTERN, resolver);
        setSystemProperty(LoggingSystemProperty.FILE_PATTERN, resolver);
        setSystemProperty(LoggingSystemProperty.LEVEL_PATTERN, resolver);
        setSystemProperty(LoggingSystemProperty.DATEFORMAT_PATTERN, resolver);
        setSystemProperty(LoggingSystemProperty.CORRELATION_PATTERN, resolver);
        if (logFile != null) {
            logFile.applyToSystemProperties();
        }
    }

    private void setApplicationNameSystemProperty(PropertyResolver resolver) {
        if (((Boolean) resolver.getProperty("logging.include-application-name", Boolean.class, Boolean.TRUE)).booleanValue()) {
            String applicationName = resolver.getProperty("spring.application.name");
            if (StringUtils.hasText(applicationName)) {
                setSystemProperty(LoggingSystemProperty.APPLICATION_NAME.getEnvironmentVariableName(), "[%s] ".formatted(applicationName));
            }
        }
    }

    private void setSystemProperty(LoggingSystemProperty property, PropertyResolver resolver) {
        setSystemProperty(property, resolver, (String) null);
    }

    private void setSystemProperty(LoggingSystemProperty property, PropertyResolver resolver, String defaultValue) {
        String value = property.getApplicationPropertyName() != null ? resolver.getProperty(property.getApplicationPropertyName()) : null;
        String value2 = value != null ? value : this.defaultValueResolver.apply(property.getApplicationPropertyName());
        setSystemProperty(property.getEnvironmentVariableName(), value2 != null ? value2 : defaultValue);
    }

    private void setSystemProperty(LoggingSystemProperty property, String value) {
        setSystemProperty(property.getEnvironmentVariableName(), value);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    protected final void setSystemProperty(PropertyResolver resolver, String systemPropertyName, String propertyName) {
        setSystemProperty(resolver, systemPropertyName, propertyName, null);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    protected final void setSystemProperty(PropertyResolver resolver, String systemPropertyName, String propertyName, String defaultValue) {
        String value = resolver.getProperty(propertyName);
        String value2 = value != null ? value : this.defaultValueResolver.apply(systemPropertyName);
        setSystemProperty(systemPropertyName, value2 != null ? value2 : defaultValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setSystemProperty(String name, String value) {
        this.setter.accept(name, value);
    }
}
