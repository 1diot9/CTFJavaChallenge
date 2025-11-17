package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/RelativeTimeConverter.class */
public class RelativeTimeConverter extends ClassicConverter {
    long lastTimestamp = -1;
    String timesmapCache = null;

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        String str;
        long now = event.getTimeStamp();
        synchronized (this) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.timesmapCache = Long.toString(now - event.getLoggerContextVO().getBirthTime());
            }
            str = this.timesmapCache;
        }
        return str;
    }
}
