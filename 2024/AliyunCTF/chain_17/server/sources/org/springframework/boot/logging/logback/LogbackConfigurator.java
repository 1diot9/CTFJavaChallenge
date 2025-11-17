package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/LogbackConfigurator.class */
class LogbackConfigurator {
    private final LoggerContext context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LogbackConfigurator(LoggerContext context) {
        Assert.notNull(context, "Context must not be null");
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoggerContext getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getConfigurationLock() {
        return this.context.getConfigurationLock();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void conversionRule(String conversionWord, Class<? extends Converter> converterClass) {
        Assert.hasLength(conversionWord, "Conversion word must not be empty");
        Assert.notNull(converterClass, "Converter class must not be null");
        Map<String, String> registry = (Map) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (registry == null) {
            registry = new HashMap<>();
            this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, registry);
        }
        registry.put(conversionWord, converterClass.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void appender(String name, Appender<?> appender) {
        appender.setName(name);
        start(appender);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logger(String name, Level level) {
        logger(name, level, true);
    }

    void logger(String name, Level level, boolean additive) {
        logger(name, level, additive, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logger(String name, Level level, boolean additive, Appender<ILoggingEvent> appender) {
        Logger logger = this.context.getLogger(name);
        if (level != null) {
            logger.setLevel(level);
        }
        logger.setAdditive(additive);
        if (appender != null) {
            logger.addAppender(appender);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public final void root(Level level, Appender<ILoggingEvent>... appenders) {
        Logger logger = this.context.getLogger("ROOT");
        if (level != null) {
            logger.setLevel(level);
        }
        for (Appender<ILoggingEvent> appender : appenders) {
            logger.addAppender(appender);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(LifeCycle lifeCycle) {
        if (lifeCycle instanceof ContextAware) {
            ContextAware contextAware = (ContextAware) lifeCycle;
            contextAware.setContext(this.context);
        }
        lifeCycle.start();
    }
}
