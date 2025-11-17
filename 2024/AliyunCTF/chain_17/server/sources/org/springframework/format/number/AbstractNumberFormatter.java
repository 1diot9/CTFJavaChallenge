package org.springframework.format.number;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/AbstractNumberFormatter.class */
public abstract class AbstractNumberFormatter implements Formatter<Number> {
    private boolean lenient = false;

    protected abstract NumberFormat getNumberFormat(Locale locale);

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @Override // org.springframework.format.Printer
    public String print(Number number, Locale locale) {
        return getNumberFormat(locale).format(number);
    }

    @Override // org.springframework.format.Parser
    public Number parse(String text, Locale locale) throws ParseException {
        NumberFormat format = getNumberFormat(locale);
        ParsePosition position = new ParsePosition(0);
        Number number = format.parse(text, position);
        if (position.getErrorIndex() != -1) {
            throw new ParseException(text, position.getIndex());
        }
        if (!this.lenient && text.length() != position.getIndex()) {
            throw new ParseException(text, position.getIndex());
        }
        return number;
    }
}
