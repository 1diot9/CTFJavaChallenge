package org.springframework.boot.logging;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggingSystem.class */
public abstract class LoggingSystem {
    public static final String NONE = "none";
    public static final String ROOT_LOGGER_NAME = "ROOT";
    public static final String EXPECT_CORRELATION_ID_PROPERTY = "logging.expect-correlation-id";
    public static final String SYSTEM_PROPERTY = LoggingSystem.class.getName();
    private static final LoggingSystemFactory SYSTEM_FACTORY = LoggingSystemFactory.fromSpringFactories();

    public abstract void beforeInitialize();

    public LoggingSystemProperties getSystemProperties(ConfigurableEnvironment environment) {
        return new LoggingSystemProperties(environment);
    }

    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
    }

    public void cleanUp() {
    }

    public Runnable getShutdownHandler() {
        return null;
    }

    public Set<LogLevel> getSupportedLogLevels() {
        return EnumSet.allOf(LogLevel.class);
    }

    public void setLogLevel(String loggerName, LogLevel level) {
        throw new UnsupportedOperationException("Unable to set log level");
    }

    public List<LoggerConfiguration> getLoggerConfigurations() {
        throw new UnsupportedOperationException("Unable to get logger configurations");
    }

    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        throw new UnsupportedOperationException("Unable to get logger configuration");
    }

    public static LoggingSystem get(ClassLoader classLoader) {
        String loggingSystemClassName = System.getProperty(SYSTEM_PROPERTY);
        if (StringUtils.hasLength(loggingSystemClassName)) {
            if ("none".equals(loggingSystemClassName)) {
                return new NoOpLoggingSystem();
            }
            return get(classLoader, loggingSystemClassName);
        }
        LoggingSystem loggingSystem = SYSTEM_FACTORY.getLoggingSystem(classLoader);
        Assert.state(loggingSystem != null, "No suitable logging system located");
        return loggingSystem;
    }

    private static LoggingSystem get(ClassLoader classLoader, String loggingSystemClassName) {
        try {
            Class<?> systemClass = ClassUtils.forName(loggingSystemClassName, classLoader);
            Constructor<?> constructor = systemClass.getDeclaredConstructor(ClassLoader.class);
            constructor.setAccessible(true);
            return (LoggingSystem) constructor.newInstance(classLoader);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggingSystem$NoOpLoggingSystem.class */
    static class NoOpLoggingSystem extends LoggingSystem {
        NoOpLoggingSystem() {
        }

        @Override // org.springframework.boot.logging.LoggingSystem
        public void beforeInitialize() {
        }

        @Override // org.springframework.boot.logging.LoggingSystem
        public void setLogLevel(String loggerName, LogLevel level) {
        }

        @Override // org.springframework.boot.logging.LoggingSystem
        public List<LoggerConfiguration> getLoggerConfigurations() {
            return Collections.emptyList();
        }

        @Override // org.springframework.boot.logging.LoggingSystem
        public LoggerConfiguration getLoggerConfiguration(String loggerName) {
            return null;
        }
    }
}
