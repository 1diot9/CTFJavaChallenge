package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.springframework.format.Printer;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/TemporalAccessorPrinter.class */
public final class TemporalAccessorPrinter implements Printer<TemporalAccessor> {
    private final DateTimeFormatter formatter;

    public TemporalAccessorPrinter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override // org.springframework.format.Printer
    public String print(TemporalAccessor partial, Locale locale) {
        return DateTimeContextHolder.getFormatter(this.formatter, locale).format(partial);
    }
}
