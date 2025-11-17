package org.springframework.boot.logging.java;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/java/JavaLoggingSystem.class */
public class JavaLoggingSystem extends AbstractLoggingSystem {
    private static final AbstractLoggingSystem.LogLevels<Level> LEVELS = new AbstractLoggingSystem.LogLevels<>();
    private final Set<Logger> configuredLoggers;

    static {
        LEVELS.map(LogLevel.TRACE, Level.FINEST);
        LEVELS.map(LogLevel.DEBUG, Level.FINE);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARNING);
        LEVELS.map(LogLevel.ERROR, Level.SEVERE);
        LEVELS.map(LogLevel.FATAL, Level.SEVERE);
        LEVELS.map(LogLevel.OFF, Level.OFF);
    }

    public JavaLoggingSystem(ClassLoader classLoader) {
        super(classLoader);
        this.configuredLoggers = Collections.synchronizedSet(new HashSet());
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected String[] getStandardConfigLocations() {
        return new String[]{"logging.properties"};
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem, org.springframework.boot.logging.LoggingSystem
    public void beforeInitialize() {
        super.beforeInitialize();
        Logger.getLogger("").setLevel(Level.SEVERE);
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
        if (logFile != null) {
            loadConfiguration(getPackagedConfigFile("logging-file.properties"), logFile);
        } else {
            loadConfiguration(getPackagedConfigFile("logging.properties"), null);
        }
    }

    @Override // org.springframework.boot.logging.AbstractLoggingSystem
    protected void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        loadConfiguration(location, logFile);
    }

    protected void loadConfiguration(String location, LogFile logFile) {
        Assert.notNull(location, "Location must not be null");
        try {
            String configuration = FileCopyUtils.copyToString(new InputStreamReader(ResourceUtils.getURL(location).openStream()));
            if (logFile != null) {
                configuration = configuration.replace("${LOG_FILE}", StringUtils.cleanPath(logFile.toString()));
            }
            LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(configuration.getBytes()));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize Java logging from " + location, ex);
        }
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Set<LogLevel> getSupportedLogLevels() {
        return LEVELS.getSupported();
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void setLogLevel(String loggerName, LogLevel level) {
        if (loggerName == null || "ROOT".equals(loggerName)) {
            loggerName = "";
        }
        Logger logger = Logger.getLogger(loggerName);
        if (logger != null) {
            this.configuredLoggers.add(logger);
            logger.setLevel(LEVELS.convertSystemToNative(level));
        }
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public List<LoggerConfiguration> getLoggerConfigurations() {
        List<LoggerConfiguration> result = new ArrayList<>();
        Enumeration<String> names = LogManager.getLogManager().getLoggerNames();
        while (names.hasMoreElements()) {
            result.add(getLoggerConfiguration(names.nextElement()));
        }
        result.sort(CONFIGURATION_COMPARATOR);
        return Collections.unmodifiableList(result);
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        if (logger == null) {
            return null;
        }
        LogLevel level = LEVELS.convertNativeToSystem(logger.getLevel());
        LogLevel effectiveLevel = LEVELS.convertNativeToSystem(getEffectiveLevel(logger));
        String name = StringUtils.hasLength(logger.getName()) ? logger.getName() : "ROOT";
        return new LoggerConfiguration(name, level, effectiveLevel);
    }

    private Level getEffectiveLevel(Logger root) {
        Logger logger = root;
        while (true) {
            Logger logger2 = logger;
            if (logger2.getLevel() == null) {
                logger = logger2.getParent();
            } else {
                return logger2.getLevel();
            }
        }
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public Runnable getShutdownHandler() {
        return () -> {
            LogManager.getLogManager().reset();
        };
    }

    @Override // org.springframework.boot.logging.LoggingSystem
    public void cleanUp() {
        this.configuredLoggers.clear();
    }

    @Order(Integer.MAX_VALUE)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/java/JavaLoggingSystem$Factory.class */
    public static class Factory implements LoggingSystemFactory {
        private static final boolean PRESENT = ClassUtils.isPresent("java.util.logging.LogManager", Factory.class.getClassLoader());

        @Override // org.springframework.boot.logging.LoggingSystemFactory
        public LoggingSystem getLoggingSystem(ClassLoader classLoader) {
            if (PRESENT) {
                return new JavaLoggingSystem(classLoader);
            }
            return null;
        }
    }
}
