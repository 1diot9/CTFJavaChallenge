package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Period;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/PeriodFormatter.class */
class PeriodFormatter implements Formatter<Period> {
    @Override // org.springframework.format.Parser
    public Period parse(String text, Locale locale) throws ParseException {
        return Period.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(Period object, Locale locale) {
        return object.toString();
    }
}
