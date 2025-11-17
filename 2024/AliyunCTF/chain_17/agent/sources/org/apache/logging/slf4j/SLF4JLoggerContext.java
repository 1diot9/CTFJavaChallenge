package org.apache.logging.slf4j;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.slf4j.LoggerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-to-slf4j-2.21.1.jar:org/apache/logging/slf4j/SLF4JLoggerContext.class */
public class SLF4JLoggerContext implements LoggerContext {
    private final LoggerRegistry<ExtendedLogger> loggerRegistry = new LoggerRegistry<>();

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public Object getExternalContext() {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(final String name) {
        if (!this.loggerRegistry.hasLogger(name)) {
            this.loggerRegistry.putIfAbsent(name, null, new SLF4JLogger(name, LoggerFactory.getLogger(name)));
        }
        return this.loggerRegistry.getLogger(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public ExtendedLogger getLogger(final String name, final MessageFactory messageFactory) {
        if (!this.loggerRegistry.hasLogger(name, messageFactory)) {
            this.loggerRegistry.putIfAbsent(name, messageFactory, new SLF4JLogger(name, messageFactory, LoggerFactory.getLogger(name)));
        }
        return this.loggerRegistry.getLogger(name, messageFactory);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(final String name) {
        return this.loggerRegistry.hasLogger(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(final String name, final MessageFactory messageFactory) {
        return this.loggerRegistry.hasLogger(name, messageFactory);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContext
    public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        return this.loggerRegistry.hasLogger(name, messageFactoryClass);
    }
}
