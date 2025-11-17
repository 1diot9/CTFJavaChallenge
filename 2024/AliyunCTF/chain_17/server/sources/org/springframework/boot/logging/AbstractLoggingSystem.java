package org.springframework.boot.logging;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/AbstractLoggingSystem.class */
public abstract class AbstractLoggingSystem extends LoggingSystem {
    protected static final Comparator<LoggerConfiguration> CONFIGURATION_COMPARATOR = new LoggerConfigurationComparator("ROOT");
    private final ClassLoader classLoader;

    protected abstract String[] getStandardConfigLocations();

    protected abstract void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile);

    protected abstract void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile);

    public AbstractLoggingSystem(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        if (StringUtils.hasLength(configLocation)) {
            initializeWithSpecificConfig(initializationContext, configLocation, logFile);
        } else {
            initializeWithConventions(initializationContext, logFile);
        }
    }

    private void initializeWithSpecificConfig(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        loadConfiguration(initializationContext, SystemPropertyUtils.resolvePlaceholders(configLocation), logFile);
    }

    private void initializeWithConventions(LoggingInitializationContext initializationContext, LogFile logFile) {
        String config = getSelfInitializationConfig();
        if (config != null && logFile == null) {
            reinitialize(initializationContext);
            return;
        }
        if (config == null) {
            config = getSpringInitializationConfig();
        }
        if (config != null) {
            loadConfiguration(initializationContext, config, logFile);
        } else {
            loadDefaults(initializationContext, logFile);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getSelfInitializationConfig() {
        return findConfig(getStandardConfigLocations());
    }

    protected String getSpringInitializationConfig() {
        return findConfig(getSpringConfigLocations());
    }

    private String findConfig(String[] locations) {
        for (String location : locations) {
            ClassPathResource resource = new ClassPathResource(location, this.classLoader);
            if (resource.exists()) {
                return "classpath:" + location;
            }
        }
        return null;
    }

    protected String[] getSpringConfigLocations() {
        String[] locations = getStandardConfigLocations();
        for (int i = 0; i < locations.length; i++) {
            String extension = StringUtils.getFilenameExtension(locations[i]);
            locations[i] = locations[i].substring(0, (locations[i].length() - extension.length()) - 1) + "-spring." + extension;
        }
        return locations;
    }

    protected void reinitialize(LoggingInitializationContext initializationContext) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getPackagedConfigFile(String fileName) {
        String defaultPath = ClassUtils.getPackageName(getClass());
        return "classpath:" + (defaultPath.replace('.', '/') + "/" + fileName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void applySystemProperties(Environment environment, LogFile logFile) {
        new LoggingSystemProperties(environment, getDefaultValueResolver(environment), null).apply(logFile);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Function<String, String> getDefaultValueResolver(Environment environment) {
        String defaultLogCorrelationPattern = getDefaultLogCorrelationPattern();
        return name -> {
            if (StringUtils.hasLength(defaultLogCorrelationPattern) && LoggingSystemProperty.CORRELATION_PATTERN.getApplicationPropertyName().equals(name) && ((Boolean) environment.getProperty(LoggingSystem.EXPECT_CORRELATION_ID_PROPERTY, Boolean.class, false)).booleanValue()) {
                return defaultLogCorrelationPattern;
            }
            return null;
        };
    }

    protected String getDefaultLogCorrelationPattern() {
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/AbstractLoggingSystem$LogLevels.class */
    protected static class LogLevels<T> {
        private final Map<LogLevel, T> systemToNative = new EnumMap(LogLevel.class);
        private final Map<T, LogLevel> nativeToSystem = new HashMap();

        public void map(LogLevel system, T nativeLevel) {
            this.systemToNative.putIfAbsent(system, nativeLevel);
            this.nativeToSystem.putIfAbsent(nativeLevel, system);
        }

        public LogLevel convertNativeToSystem(T level) {
            return this.nativeToSystem.get(level);
        }

        public T convertSystemToNative(LogLevel level) {
            return this.systemToNative.get(level);
        }

        public Set<LogLevel> getSupported() {
            return new LinkedHashSet(this.nativeToSystem.values());
        }
    }
}
