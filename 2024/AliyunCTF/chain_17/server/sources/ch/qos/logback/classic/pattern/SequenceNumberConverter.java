package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/SequenceNumberConverter.class */
public class SequenceNumberConverter extends ClassicConverter {
    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (getContext() == null) {
            return;
        }
        if (getContext().getSequenceNumberGenerator() == null) {
            addWarn("It looks like no <sequenceNumberGenerator> was defined in Logback configuration.");
        }
        super.start();
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        return Long.toString(event.getSequenceNumber());
    }
}
