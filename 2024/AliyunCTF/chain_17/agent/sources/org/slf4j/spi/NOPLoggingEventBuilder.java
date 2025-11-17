package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/spi/NOPLoggingEventBuilder.class */
public class NOPLoggingEventBuilder implements LoggingEventBuilder {
    static final NOPLoggingEventBuilder SINGLETON = new NOPLoggingEventBuilder();

    private NOPLoggingEventBuilder() {
    }

    public static LoggingEventBuilder singleton() {
        return SINGLETON;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addMarker(Marker marker) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addArgument(Object p) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addKeyValue(String key, Object value) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addKeyValue(String key, Supplier<Object> value) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setCause(Throwable cause) {
        return singleton();
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log() {
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setMessage(String message) {
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message) {
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(Supplier<String> messageSupplier) {
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object arg) {
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object arg0, Object arg1) {
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object... args) {
    }
}
