package ch.qos.logback.classic.net;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/LoggingEventPreSerializationTransformer.class */
public class LoggingEventPreSerializationTransformer implements PreSerializationTransformer<ILoggingEvent> {
    @Override // ch.qos.logback.core.spi.PreSerializationTransformer
    public Serializable transform(ILoggingEvent event) {
        if (event == null) {
            return null;
        }
        if (event instanceof LoggingEvent) {
            return LoggingEventVO.build(event);
        }
        if (event instanceof LoggingEventVO) {
            return (LoggingEventVO) event;
        }
        throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
    }
}
