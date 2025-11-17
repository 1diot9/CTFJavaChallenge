package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.Marker;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/event/DefaultLoggingEvent.class */
public class DefaultLoggingEvent implements LoggingEvent {
    Logger logger;
    Level level;
    String message;
    List<Marker> markers;
    List<Object> arguments;
    List<KeyValuePair> keyValuePairs;
    Throwable throwable;
    String threadName;
    long timeStamp;
    String callerBoundary;

    public DefaultLoggingEvent(Level level, Logger logger) {
        this.logger = logger;
        this.level = level;
    }

    public void addMarker(Marker marker) {
        if (this.markers == null) {
            this.markers = new ArrayList(2);
        }
        this.markers.add(marker);
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<Marker> getMarkers() {
        return this.markers;
    }

    public void addArgument(Object p) {
        getNonNullArguments().add(p);
    }

    public void addArguments(Object... args) {
        getNonNullArguments().addAll(Arrays.asList(args));
    }

    private List<Object> getNonNullArguments() {
        if (this.arguments == null) {
            this.arguments = new ArrayList(3);
        }
        return this.arguments;
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<Object> getArguments() {
        return this.arguments;
    }

    @Override // org.slf4j.event.LoggingEvent
    public Object[] getArgumentArray() {
        if (this.arguments == null) {
            return null;
        }
        return this.arguments.toArray();
    }

    public void addKeyValue(String key, Object value) {
        getNonnullKeyValuePairs().add(new KeyValuePair(key, value));
    }

    private List<KeyValuePair> getNonnullKeyValuePairs() {
        if (this.keyValuePairs == null) {
            this.keyValuePairs = new ArrayList(4);
        }
        return this.keyValuePairs;
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<KeyValuePair> getKeyValuePairs() {
        return this.keyValuePairs;
    }

    public void setThrowable(Throwable cause) {
        this.throwable = cause;
    }

    @Override // org.slf4j.event.LoggingEvent
    public Level getLevel() {
        return this.level;
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getLoggerName() {
        return this.logger.getName();
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override // org.slf4j.event.LoggingEvent
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getThreadName() {
        return this.threadName;
    }

    @Override // org.slf4j.event.LoggingEvent
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCallerBoundary(String fqcn) {
        this.callerBoundary = fqcn;
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getCallerBoundary() {
        return this.callerBoundary;
    }
}
