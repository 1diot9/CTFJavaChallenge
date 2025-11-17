package org.slf4j.spi;

import org.slf4j.event.LoggingEvent;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/spi/LoggingEventAware.class */
public interface LoggingEventAware {
    void log(LoggingEvent loggingEvent);
}
