package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/NumberStyleFormatter.class */
public class NumberStyleFormatter extends AbstractNumberFormatter {

    @Nullable
    private String pattern;

    public NumberStyleFormatter() {
    }

    public NumberStyleFormatter(String pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override // org.springframework.format.number.AbstractNumberFormatter
    public NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getInstance(locale);
        if (!(format instanceof DecimalFormat)) {
            if (this.pattern != null) {
                throw new IllegalStateException("Cannot support pattern for non-DecimalFormat: " + format);
            }
            return format;
        }
        DecimalFormat decimalFormat = (DecimalFormat) format;
        decimalFormat.setParseBigDecimal(true);
        if (this.pattern != null) {
            decimalFormat.applyPattern(this.pattern);
        }
        return decimalFormat;
    }
}
