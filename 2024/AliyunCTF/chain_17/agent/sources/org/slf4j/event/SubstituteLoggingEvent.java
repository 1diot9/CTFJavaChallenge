package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/event/SubstituteLoggingEvent.class */
public class SubstituteLoggingEvent implements LoggingEvent {
    Level level;
    List<Marker> markers;
    String loggerName;
    SubstituteLogger logger;
    String threadName;
    String message;
    Object[] argArray;
    List<KeyValuePair> keyValuePairList;
    long timeStamp;
    Throwable throwable;

    @Override // org.slf4j.event.LoggingEvent
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<Marker> getMarkers() {
        return this.markers;
    }

    public void addMarker(Marker marker) {
        if (marker == null) {
            return;
        }
        if (this.markers == null) {
            this.markers = new ArrayList(2);
        }
        this.markers.add(marker);
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public SubstituteLogger getLogger() {
        return this.logger;
    }

    public void setLogger(SubstituteLogger logger) {
        this.logger = logger;
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override // org.slf4j.event.LoggingEvent
    public Object[] getArgumentArray() {
        return this.argArray;
    }

    public void setArgumentArray(Object[] argArray) {
        this.argArray = argArray;
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<Object> getArguments() {
        if (this.argArray == null) {
            return null;
        }
        return Arrays.asList(this.argArray);
    }

    @Override // org.slf4j.event.LoggingEvent
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override // org.slf4j.event.LoggingEvent
    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override // org.slf4j.event.LoggingEvent
    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override // org.slf4j.event.LoggingEvent
    public List<KeyValuePair> getKeyValuePairs() {
        return this.keyValuePairList;
    }
}
