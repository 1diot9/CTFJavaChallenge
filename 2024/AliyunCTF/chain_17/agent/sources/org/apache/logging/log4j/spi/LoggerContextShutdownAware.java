package org.apache.logging.log4j.spi;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/LoggerContextShutdownAware.class */
public interface LoggerContextShutdownAware {
    void contextShutdown(LoggerContext loggerContext);
}
