package org.springframework.boot.convert;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/IsoOffsetFormatter.class */
class IsoOffsetFormatter implements Formatter<OffsetDateTime> {
    @Override // org.springframework.format.Printer
    public String print(OffsetDateTime object, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(object);
    }

    @Override // org.springframework.format.Parser
    public OffsetDateTime parse(String text, Locale locale) throws ParseException {
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
