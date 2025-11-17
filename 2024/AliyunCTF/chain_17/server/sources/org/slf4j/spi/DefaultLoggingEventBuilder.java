package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/spi/DefaultLoggingEventBuilder.class */
public class DefaultLoggingEventBuilder implements LoggingEventBuilder, CallerBoundaryAware {
    static String DLEB_FQCN = DefaultLoggingEventBuilder.class.getName();
    protected DefaultLoggingEvent loggingEvent;
    protected Logger logger;

    public DefaultLoggingEventBuilder(Logger logger, Level level) {
        this.logger = logger;
        this.loggingEvent = new DefaultLoggingEvent(level, logger);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addMarker(Marker marker) {
        this.loggingEvent.addMarker(marker);
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setCause(Throwable t) {
        this.loggingEvent.setThrowable(t);
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addArgument(Object p) {
        this.loggingEvent.addArgument(p);
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        this.loggingEvent.addArgument(objectSupplier.get());
        return this;
    }

    @Override // org.slf4j.spi.CallerBoundaryAware
    public void setCallerBoundary(String fqcn) {
        this.loggingEvent.setCallerBoundary(fqcn);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log() {
        log(this.loggingEvent);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setMessage(String message) {
        this.loggingEvent.setMessage(message);
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        this.loggingEvent.setMessage(messageSupplier.get());
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message) {
        this.loggingEvent.setMessage(message);
        log(this.loggingEvent);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object arg) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArgument(arg);
        log(this.loggingEvent);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object arg0, Object arg1) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArgument(arg0);
        this.loggingEvent.addArgument(arg1);
        log(this.loggingEvent);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(String message, Object... args) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArguments(args);
        log(this.loggingEvent);
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public void log(Supplier<String> messageSupplier) {
        if (messageSupplier == null) {
            log((String) null);
        } else {
            log(messageSupplier.get());
        }
    }

    protected void log(LoggingEvent aLoggingEvent) {
        setCallerBoundary(DLEB_FQCN);
        if (this.logger instanceof LoggingEventAware) {
            ((LoggingEventAware) this.logger).log(aLoggingEvent);
        } else {
            logViaPublicSLF4JLoggerAPI(aLoggingEvent);
        }
    }

    private void logViaPublicSLF4JLoggerAPI(LoggingEvent aLoggingEvent) {
        Object[] argArray = aLoggingEvent.getArgumentArray();
        int argLen = argArray == null ? 0 : argArray.length;
        Throwable t = aLoggingEvent.getThrowable();
        int tLen = t == null ? 0 : 1;
        String msg = aLoggingEvent.getMessage();
        Object[] combinedArguments = new Object[argLen + tLen];
        if (argArray != null) {
            System.arraycopy(argArray, 0, combinedArguments, 0, argLen);
        }
        if (t != null) {
            combinedArguments[argLen] = t;
        }
        String msg2 = mergeMarkersAndKeyValuePairs(aLoggingEvent, msg);
        switch (aLoggingEvent.getLevel()) {
            case TRACE:
                this.logger.trace(msg2, combinedArguments);
                return;
            case DEBUG:
                this.logger.debug(msg2, combinedArguments);
                return;
            case INFO:
                this.logger.info(msg2, combinedArguments);
                return;
            case WARN:
                this.logger.warn(msg2, combinedArguments);
                return;
            case ERROR:
                this.logger.error(msg2, combinedArguments);
                return;
            default:
                return;
        }
    }

    private String mergeMarkersAndKeyValuePairs(LoggingEvent aLoggingEvent, String msg) {
        StringBuilder sb = null;
        if (aLoggingEvent.getMarkers() != null) {
            sb = new StringBuilder();
            for (Marker marker : aLoggingEvent.getMarkers()) {
                sb.append(marker);
                sb.append(' ');
            }
        }
        if (aLoggingEvent.getKeyValuePairs() != null) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            for (KeyValuePair kvp : aLoggingEvent.getKeyValuePairs()) {
                sb.append(kvp.key);
                sb.append('=');
                sb.append(kvp.value);
                sb.append(' ');
            }
        }
        if (sb != null) {
            sb.append(msg);
            return sb.toString();
        }
        return msg;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addKeyValue(String key, Object value) {
        this.loggingEvent.addKeyValue(key, value);
        return this;
    }

    @Override // org.slf4j.spi.LoggingEventBuilder
    public LoggingEventBuilder addKeyValue(String key, Supplier<Object> value) {
        this.loggingEvent.addKeyValue(key, value.get());
        return this;
    }
}
