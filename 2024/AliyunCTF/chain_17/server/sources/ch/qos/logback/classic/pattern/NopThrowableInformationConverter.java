package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/NopThrowableInformationConverter.class */
public class NopThrowableInformationConverter extends ThrowableHandlingConverter {
    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        return "";
    }
}
