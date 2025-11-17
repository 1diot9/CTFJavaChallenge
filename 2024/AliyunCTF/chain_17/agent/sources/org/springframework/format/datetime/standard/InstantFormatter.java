package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/InstantFormatter.class */
public class InstantFormatter implements Formatter<Instant> {
    @Override // org.springframework.format.Parser
    public Instant parse(String text, Locale locale) throws ParseException {
        try {
            return Instant.ofEpochMilli(Long.parseLong(text));
        } catch (NumberFormatException e) {
            if (text.length() > 0 && Character.isAlphabetic(text.charAt(0))) {
                return Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(text));
            }
            return Instant.parse(text);
        }
    }

    @Override // org.springframework.format.Printer
    public String print(Instant object, Locale locale) {
        return object.toString();
    }
}
