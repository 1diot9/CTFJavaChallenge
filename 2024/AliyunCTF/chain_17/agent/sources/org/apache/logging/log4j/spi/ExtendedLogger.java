package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/ExtendedLogger.class */
public interface ExtendedLogger extends Logger {
    boolean isEnabled(Level level, Marker marker, Message message, Throwable t);

    boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t);

    boolean isEnabled(Level level, Marker marker, Object message, Throwable t);

    boolean isEnabled(Level level, Marker marker, String message, Throwable t);

    boolean isEnabled(Level level, Marker marker, String message);

    boolean isEnabled(Level level, Marker marker, String message, Object... params);

    boolean isEnabled(Level level, Marker marker, String message, Object p0);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8);

    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    void logIfEnabled(String fqcn, Level level, Marker marker, Message message, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, CharSequence message, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, Object message, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object... params);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, MessageSupplier msgSupplier, Throwable t);

    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Supplier<?>... paramSuppliers);

    void logIfEnabled(String fqcn, Level level, Marker marker, Supplier<?> msgSupplier, Throwable t);
}
