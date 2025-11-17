package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/LogBuilder.class */
public interface LogBuilder {
    public static final LogBuilder NOOP = new LogBuilder() { // from class: org.apache.logging.log4j.LogBuilder.1
    };

    default LogBuilder withMarker(Marker marker) {
        return this;
    }

    default LogBuilder withThrowable(Throwable throwable) {
        return this;
    }

    default LogBuilder withLocation() {
        return this;
    }

    default LogBuilder withLocation(StackTraceElement location) {
        return this;
    }

    default void log(CharSequence message) {
    }

    default void log(String message) {
    }

    default void log(String message, Object... params) {
    }

    default void log(String message, Supplier<?>... params) {
    }

    default void log(Message message) {
    }

    default void log(Supplier<Message> messageSupplier) {
    }

    default Message logAndGet(final Supplier<Message> messageSupplier) {
        return null;
    }

    default void log(Object message) {
    }

    default void log(String message, Object p0) {
    }

    default void log(String message, Object p0, Object p1) {
    }

    default void log(String message, Object p0, Object p1, Object p2) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
    }

    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
    }

    default void log() {
    }
}
