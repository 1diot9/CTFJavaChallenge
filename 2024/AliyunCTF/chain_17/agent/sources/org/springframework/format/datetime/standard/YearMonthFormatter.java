package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.YearMonth;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/YearMonthFormatter.class */
class YearMonthFormatter implements Formatter<YearMonth> {
    @Override // org.springframework.format.Parser
    public YearMonth parse(String text, Locale locale) throws ParseException {
        return YearMonth.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(YearMonth object, Locale locale) {
        return object.toString();
    }
}
