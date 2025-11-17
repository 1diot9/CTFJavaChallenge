package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.StackLocatorUtil;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/ExtendedLoggerWrapper.class */
public class ExtendedLoggerWrapper extends AbstractLogger {
    private static final long serialVersionUID = 1;
    protected final ExtendedLogger logger;

    public ExtendedLoggerWrapper(final ExtendedLogger logger, final String name, final MessageFactory messageFactory) {
        super(name, messageFactory);
        this.logger = logger;
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        return this.logger.getLevel();
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        return this.logger.isEnabled(level, marker, message);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        return this.logger.isEnabled(level, marker, message, params);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        return this.logger.isEnabled(level, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {
        return this.logger.isEnabled(level, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return this.logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        return this.logger.isEnabled(level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message, final Throwable t) {
        if ((this.logger instanceof LocationAwareLogger) && requiresLocation()) {
            ((LocationAwareLogger) this.logger).logMessage(level, marker, fqcn, StackLocatorUtil.calcLocation(fqcn), message, t);
        } else {
            this.logger.logMessage(fqcn, level, marker, message, t);
        }
    }
}
