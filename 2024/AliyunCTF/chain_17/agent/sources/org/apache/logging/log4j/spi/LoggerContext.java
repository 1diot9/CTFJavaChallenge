package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/LoggerContext.class */
public interface LoggerContext {
    public static final LoggerContext[] EMPTY_ARRAY = new LoggerContext[0];

    Object getExternalContext();

    ExtendedLogger getLogger(String name);

    ExtendedLogger getLogger(String name, MessageFactory messageFactory);

    boolean hasLogger(String name);

    boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass);

    boolean hasLogger(String name, MessageFactory messageFactory);

    default ExtendedLogger getLogger(Class<?> cls) {
        String canonicalName = cls.getCanonicalName();
        return getLogger(canonicalName != null ? canonicalName : cls.getName());
    }

    default ExtendedLogger getLogger(Class<?> cls, MessageFactory messageFactory) {
        String canonicalName = cls.getCanonicalName();
        return getLogger(canonicalName != null ? canonicalName : cls.getName(), messageFactory);
    }

    default LoggerRegistry<? extends Logger> getLoggerRegistry() {
        return null;
    }

    default Object getObject(String key) {
        return null;
    }

    default Object putObject(String key, Object value) {
        return null;
    }

    default Object putObjectIfAbsent(String key, Object value) {
        return null;
    }

    default Object removeObject(String key) {
        return null;
    }

    default boolean removeObject(String key, Object value) {
        return false;
    }
}
