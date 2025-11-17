package org.apache.logging.slf4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/log4j-to-slf4j-2.21.1.jar:org/apache/logging/slf4j/SLF4JLogBuilder.class */
public class SLF4JLogBuilder implements LogBuilder {
    private static Message EMPTY_MESSAGE = new SimpleMessage("");
    private static final String FQCN = SLF4JLogBuilder.class.getName();
    private static final Logger LOGGER = StatusLogger.getLogger();
    private ExtendedLogger logger;
    private Level level;
    private Marker marker;
    private Throwable throwable;
    private volatile boolean inUse;
    private final long threadId;

    public SLF4JLogBuilder(final SLF4JLogger logger, final Level level) {
        this.logger = logger;
        this.level = level;
        this.threadId = Thread.currentThread().getId();
        this.inUse = level != null;
    }

    public SLF4JLogBuilder() {
        this(null, null);
    }

    public LogBuilder reset(final SLF4JLogger logger, final Level level) {
        this.logger = logger;
        this.level = level;
        this.marker = null;
        this.throwable = null;
        this.inUse = true;
        return this;
    }

    public boolean isInUse() {
        return this.inUse;
    }

    private boolean isValid() {
        if (!this.inUse) {
            LOGGER.warn("Attempt to reuse LogBuilder was ignored. {}", StackLocatorUtil.getCallerClass(2));
            return false;
        }
        if (this.threadId != Thread.currentThread().getId()) {
            LOGGER.warn("LogBuilder can only be used on the owning thread. {}", StackLocatorUtil.getCallerClass(2));
            return false;
        }
        return true;
    }

    private void logMessage(Message message) {
        try {
            this.logger.logMessage(FQCN, this.level, this.marker, message, this.throwable);
        } finally {
            this.inUse = false;
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withMarker(final Marker marker) {
        this.marker = marker;
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withThrowable(final Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withLocation() {
        LOGGER.info("Call to withLocation() ignored since SLF4J does not support setting location information.");
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withLocation(final StackTraceElement location) {
        return withLocation();
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(CharSequence message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object... params) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, params));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Supplier<?>... params) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, LambdaUtil.getAll(params)));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(Message message) {
        if (isValid()) {
            logMessage(message);
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(final Supplier<Message> messageSupplier) {
        if (isValid()) {
            logMessage(messageSupplier.get());
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public Message logAndGet(final Supplier<Message> messageSupplier) {
        Message message = null;
        if (isValid()) {
            Message message2 = messageSupplier.get();
            message = message2;
            logMessage(message2);
        }
        return message;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(Object message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log() {
        if (isValid()) {
            logMessage(EMPTY_MESSAGE);
        }
    }
}
