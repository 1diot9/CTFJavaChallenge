package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/LocalSequenceNumberConverter.class */
public class LocalSequenceNumberConverter extends ClassicConverter {
    AtomicLong sequenceNumber = new AtomicLong(System.currentTimeMillis());

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        return Long.toString(this.sequenceNumber.getAndIncrement());
    }
}
