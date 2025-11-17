package org.apache.logging.log4j.spi;

import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/LoggerContextShutdownEnabled.class */
public interface LoggerContextShutdownEnabled {
    void addShutdownListener(LoggerContextShutdownAware listener);

    List<LoggerContextShutdownAware> getListeners();
}
