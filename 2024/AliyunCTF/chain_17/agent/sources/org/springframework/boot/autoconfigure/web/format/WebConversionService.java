package org.springframework.boot.autoconfigure.web.format;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/format/WebConversionService.class */
public class WebConversionService extends DefaultFormattingConversionService {
    private static final boolean JSR_354_PRESENT = ClassUtils.isPresent("javax.money.MonetaryAmount", WebConversionService.class.getClassLoader());

    public WebConversionService(DateTimeFormatters dateTimeFormatters) {
        super(false);
        if (dateTimeFormatters.isCustomized()) {
            addFormatters(dateTimeFormatters);
        } else {
            addDefaultFormatters(this);
        }
    }

    private void addFormatters(DateTimeFormatters dateTimeFormatters) {
        addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (JSR_354_PRESENT) {
            addFormatter(new CurrencyUnitFormatter());
            addFormatter(new MonetaryAmountFormatter());
            addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
        }
        registerJsr310(dateTimeFormatters);
        registerJavaDate(dateTimeFormatters);
    }

    private void registerJsr310(DateTimeFormatters dateTimeFormatters) {
        DateTimeFormatterRegistrar dateTime = new DateTimeFormatterRegistrar();
        Objects.requireNonNull(dateTimeFormatters);
        Supplier<DateTimeFormatter> supplier = dateTimeFormatters::getDateFormatter;
        Objects.requireNonNull(dateTime);
        configure(supplier, dateTime::setDateFormatter);
        Objects.requireNonNull(dateTimeFormatters);
        Supplier<DateTimeFormatter> supplier2 = dateTimeFormatters::getTimeFormatter;
        Objects.requireNonNull(dateTime);
        configure(supplier2, dateTime::setTimeFormatter);
        Objects.requireNonNull(dateTimeFormatters);
        Supplier<DateTimeFormatter> supplier3 = dateTimeFormatters::getDateTimeFormatter;
        Objects.requireNonNull(dateTime);
        configure(supplier3, dateTime::setDateTimeFormatter);
        dateTime.registerFormatters(this);
    }

    private void configure(Supplier<DateTimeFormatter> supplier, Consumer<DateTimeFormatter> consumer) {
        DateTimeFormatter formatter = supplier.get();
        if (formatter != null) {
            consumer.accept(formatter);
        }
    }

    private void registerJavaDate(DateTimeFormatters dateTimeFormatters) {
        DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
        String datePattern = dateTimeFormatters.getDatePattern();
        if (datePattern != null) {
            DateFormatter dateFormatter = new DateFormatter(datePattern);
            dateFormatterRegistrar.setFormatter(dateFormatter);
        }
        dateFormatterRegistrar.registerFormatters(this);
    }
}
