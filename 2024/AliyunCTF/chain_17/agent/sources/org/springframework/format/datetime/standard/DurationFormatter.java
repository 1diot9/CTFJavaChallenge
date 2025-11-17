package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DurationFormatter.class */
class DurationFormatter implements Formatter<Duration> {
    @Override // org.springframework.format.Parser
    public Duration parse(String text, Locale locale) throws ParseException {
        return Duration.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(Duration object, Locale locale) {
        return object.toString();
    }
}
