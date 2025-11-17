package org.springframework.boot.logging.logback;

import ch.qos.logback.core.util.FileSize;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingSystemProperties;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.unit.DataSize;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/LogbackLoggingSystemProperties.class */
public class LogbackLoggingSystemProperties extends LoggingSystemProperties {
    private static final boolean JBOSS_LOGGING_PRESENT = ClassUtils.isPresent("org.jboss.logging.Logger", LogbackLoggingSystemProperties.class.getClassLoader());

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String ROLLINGPOLICY_FILE_NAME_PATTERN = RollingPolicySystemProperty.FILE_NAME_PATTERN.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String ROLLINGPOLICY_CLEAN_HISTORY_ON_START = RollingPolicySystemProperty.CLEAN_HISTORY_ON_START.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String ROLLINGPOLICY_MAX_FILE_SIZE = RollingPolicySystemProperty.MAX_FILE_SIZE.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String ROLLINGPOLICY_TOTAL_SIZE_CAP = RollingPolicySystemProperty.TOTAL_SIZE_CAP.getEnvironmentVariableName();

    @Deprecated(since = "3.2.0", forRemoval = true)
    public static final String ROLLINGPOLICY_MAX_HISTORY = RollingPolicySystemProperty.MAX_HISTORY.getEnvironmentVariableName();

    public LogbackLoggingSystemProperties(Environment environment) {
        super(environment);
    }

    public LogbackLoggingSystemProperties(Environment environment, BiConsumer<String, String> setter) {
        super(environment, setter);
    }

    public LogbackLoggingSystemProperties(Environment environment, Function<String, String> defaultValueResolver, BiConsumer<String, String> setter) {
        super(environment, defaultValueResolver, setter);
    }

    @Override // org.springframework.boot.logging.LoggingSystemProperties
    protected Charset getDefaultCharset() {
        return Charset.defaultCharset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.logging.LoggingSystemProperties
    public void apply(LogFile logFile, PropertyResolver resolver) {
        super.apply(logFile, resolver);
        applyJBossLoggingProperties();
        applyRollingPolicyProperties(resolver);
    }

    private void applyJBossLoggingProperties() {
        if (JBOSS_LOGGING_PRESENT) {
            setSystemProperty("org.jboss.logging.provider", "slf4j");
        }
    }

    private void applyRollingPolicyProperties(PropertyResolver resolver) {
        applyRollingPolicy(RollingPolicySystemProperty.FILE_NAME_PATTERN, resolver);
        applyRollingPolicy(RollingPolicySystemProperty.CLEAN_HISTORY_ON_START, resolver);
        applyRollingPolicy(RollingPolicySystemProperty.MAX_FILE_SIZE, resolver, DataSize.class);
        applyRollingPolicy(RollingPolicySystemProperty.TOTAL_SIZE_CAP, resolver, DataSize.class);
        applyRollingPolicy(RollingPolicySystemProperty.MAX_HISTORY, resolver);
    }

    private void applyRollingPolicy(RollingPolicySystemProperty property, PropertyResolver resolver) {
        applyRollingPolicy(property, resolver, String.class);
    }

    private <T> void applyRollingPolicy(RollingPolicySystemProperty property, PropertyResolver resolver, Class<T> type) {
        Object obj;
        Object property2 = getProperty(resolver, property.getApplicationPropertyName(), type);
        Object property3 = property2 != null ? property2 : getProperty(resolver, property.getDeprecatedApplicationPropertyName(), type);
        if (property3 != null) {
            if (property3 instanceof DataSize) {
                DataSize dataSize = (DataSize) property3;
                obj = Long.valueOf(dataSize.toBytes());
            } else {
                obj = property3;
            }
            String stringValue = String.valueOf(obj);
            setSystemProperty(property.getEnvironmentVariableName(), stringValue);
        }
    }

    private <T> T getProperty(PropertyResolver propertyResolver, String str, Class<T> cls) {
        try {
            return (T) propertyResolver.getProperty(str, cls);
        } catch (ConversionFailedException | ConverterNotFoundException e) {
            if (cls != DataSize.class) {
                throw e;
            }
            return (T) DataSize.ofBytes(FileSize.valueOf(propertyResolver.getProperty(str)).getSize());
        }
    }
}
