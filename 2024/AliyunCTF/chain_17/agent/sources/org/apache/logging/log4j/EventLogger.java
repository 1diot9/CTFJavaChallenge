package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/EventLogger.class */
public final class EventLogger {
    public static final Marker EVENT_MARKER = MarkerManager.getMarker("EVENT");
    private static final String FQCN = EventLogger.class.getName();
    private static final String NAME = "EventLogger";
    private static final ExtendedLogger LOGGER = LogManager.getContext(false).getLogger(NAME);

    private EventLogger() {
    }

    public static void logEvent(final StructuredDataMessage msg) {
        LOGGER.logIfEnabled(FQCN, Level.OFF, EVENT_MARKER, (Message) msg, (Throwable) null);
    }

    public static void logEvent(final StructuredDataMessage msg, final Level level) {
        LOGGER.logIfEnabled(FQCN, level, EVENT_MARKER, (Message) msg, (Throwable) null);
    }
}
