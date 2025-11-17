package org.slf4j.event;

import java.util.List;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/event/LoggingEvent.class */
public interface LoggingEvent {
    Level getLevel();

    String getLoggerName();

    String getMessage();

    List<Object> getArguments();

    Object[] getArgumentArray();

    List<Marker> getMarkers();

    List<KeyValuePair> getKeyValuePairs();

    Throwable getThrowable();

    long getTimeStamp();

    String getThreadName();

    default String getCallerBoundary() {
        return null;
    }
}
