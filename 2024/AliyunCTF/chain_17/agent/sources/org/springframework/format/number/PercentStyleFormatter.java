package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/PercentStyleFormatter.class */
public class PercentStyleFormatter extends AbstractNumberFormatter {
    @Override // org.springframework.format.number.AbstractNumberFormatter
    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        if (format instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) format;
            decimalFormat.setParseBigDecimal(true);
        }
        return format;
    }
}
