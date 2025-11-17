package org.springframework.format.number.money;

import java.text.ParseException;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/money/Jsr354NumberFormatAnnotationFormatterFactory.class */
public class Jsr354NumberFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<NumberFormat> {
    private static final String CURRENCY_CODE_PATTERN = "¤¤";

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(NumberFormat annotation, Class fieldType) {
        return getParser2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(NumberFormat annotation, Class fieldType) {
        return getPrinter2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(MonetaryAmount.class);
    }

    /* renamed from: getPrinter, reason: avoid collision after fix types in other method */
    public Printer<MonetaryAmount> getPrinter2(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    /* renamed from: getParser, reason: avoid collision after fix types in other method */
    public Parser<MonetaryAmount> getParser2(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    private Formatter<MonetaryAmount> configureFormatterFrom(NumberFormat annotation) {
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            return new PatternDecoratingFormatter(pattern);
        }
        NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.NUMBER) {
            return new NumberDecoratingFormatter(new NumberStyleFormatter());
        }
        if (style == NumberFormat.Style.PERCENT) {
            return new NumberDecoratingFormatter(new PercentStyleFormatter());
        }
        return new NumberDecoratingFormatter(new CurrencyStyleFormatter());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/money/Jsr354NumberFormatAnnotationFormatterFactory$NumberDecoratingFormatter.class */
    public static class NumberDecoratingFormatter implements Formatter<MonetaryAmount> {
        private final Formatter<Number> numberFormatter;

        public NumberDecoratingFormatter(Formatter<Number> numberFormatter) {
            this.numberFormatter = numberFormatter;
        }

        @Override // org.springframework.format.Printer
        public String print(MonetaryAmount object, Locale locale) {
            return this.numberFormatter.print(object.getNumber(), locale);
        }

        @Override // org.springframework.format.Parser
        public MonetaryAmount parse(String text, Locale locale) throws ParseException {
            CurrencyUnit currencyUnit = Monetary.getCurrency(locale, new String[0]);
            Number numberValue = this.numberFormatter.parse(text, locale);
            return Monetary.getDefaultAmountFactory().setNumber(numberValue).setCurrency(currencyUnit).create();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/money/Jsr354NumberFormatAnnotationFormatterFactory$PatternDecoratingFormatter.class */
    public static class PatternDecoratingFormatter implements Formatter<MonetaryAmount> {
        private final String pattern;

        public PatternDecoratingFormatter(String pattern) {
            this.pattern = pattern;
        }

        @Override // org.springframework.format.Printer
        public String print(MonetaryAmount object, Locale locale) {
            CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();
            formatter.setCurrency(Currency.getInstance(object.getCurrency().getCurrencyCode()));
            formatter.setPattern(this.pattern);
            return formatter.print((Number) object.getNumber(), locale);
        }

        @Override // org.springframework.format.Parser
        public MonetaryAmount parse(String text, Locale locale) throws ParseException {
            CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();
            Currency currency = determineCurrency(text, locale);
            CurrencyUnit currencyUnit = Monetary.getCurrency(currency.getCurrencyCode(), new String[0]);
            formatter.setCurrency(currency);
            formatter.setPattern(this.pattern);
            Number numberValue = formatter.parse(text, locale);
            return Monetary.getDefaultAmountFactory().setNumber(numberValue).setCurrency(currencyUnit).create();
        }

        private Currency determineCurrency(String text, Locale locale) {
            try {
                if (text.length() < 3) {
                    return Currency.getInstance(locale);
                }
                if (this.pattern.startsWith(Jsr354NumberFormatAnnotationFormatterFactory.CURRENCY_CODE_PATTERN)) {
                    return Currency.getInstance(text.substring(0, 3));
                }
                if (this.pattern.endsWith(Jsr354NumberFormatAnnotationFormatterFactory.CURRENCY_CODE_PATTERN)) {
                    return Currency.getInstance(text.substring(text.length() - 3));
                }
                return Currency.getInstance(locale);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Cannot determine currency for number value [" + text + "]", ex);
            }
        }
    }
}
