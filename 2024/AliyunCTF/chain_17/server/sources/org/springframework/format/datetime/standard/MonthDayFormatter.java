package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.MonthDay;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/MonthDayFormatter.class */
class MonthDayFormatter implements Formatter<MonthDay> {
    @Override // org.springframework.format.Parser
    public MonthDay parse(String text, Locale locale) throws ParseException {
        return MonthDay.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(MonthDay object, Locale locale) {
        return object.toString();
    }
}
