package org.springframework.format.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/CurrencyStyleFormatter.class */
public class CurrencyStyleFormatter extends AbstractNumberFormatter {
    private int fractionDigits = 2;

    @Nullable
    private RoundingMode roundingMode;

    @Nullable
    private Currency currency;

    @Nullable
    private String pattern;

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override // org.springframework.format.number.AbstractNumberFormatter, org.springframework.format.Parser
    public BigDecimal parse(String text, Locale locale) throws ParseException {
        BigDecimal decimal;
        BigDecimal decimal2 = (BigDecimal) super.parse(text, locale);
        if (this.roundingMode != null) {
            decimal = decimal2.setScale(this.fractionDigits, this.roundingMode);
        } else {
            decimal = decimal2.setScale(this.fractionDigits);
        }
        return decimal;
    }

    @Override // org.springframework.format.number.AbstractNumberFormatter
    protected NumberFormat getNumberFormat(Locale locale) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        format.setParseBigDecimal(true);
        format.setMaximumFractionDigits(this.fractionDigits);
        format.setMinimumFractionDigits(this.fractionDigits);
        if (this.roundingMode != null) {
            format.setRoundingMode(this.roundingMode);
        }
        if (this.currency != null) {
            format.setCurrency(this.currency);
        }
        if (this.pattern != null) {
            format.applyPattern(this.pattern);
        }
        return format;
    }
}
