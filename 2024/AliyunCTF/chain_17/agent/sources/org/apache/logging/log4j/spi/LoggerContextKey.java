package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/LoggerContextKey.class */
public class LoggerContextKey {
    public static String create(final String name) {
        return create(name, AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS);
    }

    public static String create(final String name, final MessageFactory messageFactory) {
        return create(name, (Class<? extends MessageFactory>) (messageFactory != null ? messageFactory.getClass() : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS));
    }

    public static String create(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        Class<? extends MessageFactory> mfClass = messageFactoryClass != null ? messageFactoryClass : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
        return name + "." + mfClass.getName();
    }
}
