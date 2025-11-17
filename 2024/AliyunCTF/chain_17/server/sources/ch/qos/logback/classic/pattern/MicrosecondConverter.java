package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/MicrosecondConverter.class */
public class MicrosecondConverter extends ClassicConverter {
    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        int nanos = event.getNanoseconds();
        int millis_and_micros = nanos / 1000;
        int micros = millis_and_micros % 1000;
        if (micros >= 100) {
            return Integer.toString(micros);
        }
        if (micros >= 10) {
            return "0" + Integer.toString(micros);
        }
        return "00" + Integer.toString(micros);
    }
}
