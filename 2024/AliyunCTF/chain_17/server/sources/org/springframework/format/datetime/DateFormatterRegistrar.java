package org.springframework.format.datetime;

import java.util.Calendar;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar.class */
public class DateFormatterRegistrar implements FormatterRegistrar {

    @Nullable
    private DateFormatter dateFormatter;

    public void setFormatter(DateFormatter dateFormatter) {
        Assert.notNull(dateFormatter, "DateFormatter must not be null");
        this.dateFormatter = dateFormatter;
    }

    @Override // org.springframework.format.FormatterRegistrar
    public void registerFormatters(FormatterRegistry registry) {
        addDateConverters(registry);
        if (this.dateFormatter != null) {
            registry.addFormatter(this.dateFormatter);
            registry.addFormatterForFieldType(Calendar.class, this.dateFormatter);
        }
        registry.addFormatterForFieldAnnotation(new DateTimeFormatAnnotationFormatterFactory());
    }

    public static void addDateConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new DateToLongConverter());
        converterRegistry.addConverter(new DateToCalendarConverter());
        converterRegistry.addConverter(new CalendarToDateConverter());
        converterRegistry.addConverter(new CalendarToLongConverter());
        converterRegistry.addConverter(new LongToDateConverter());
        converterRegistry.addConverter(new LongToCalendarConverter());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$DateToLongConverter.class */
    public static class DateToLongConverter implements Converter<Date, Long> {
        private DateToLongConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Long convert(Date source) {
            return Long.valueOf(source.getTime());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$DateToCalendarConverter.class */
    public static class DateToCalendarConverter implements Converter<Date, Calendar> {
        private DateToCalendarConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Calendar convert(Date source) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(source);
            return calendar;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$CalendarToDateConverter.class */
    public static class CalendarToDateConverter implements Converter<Calendar, Date> {
        private CalendarToDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Date convert(Calendar source) {
            return source.getTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$CalendarToLongConverter.class */
    public static class CalendarToLongConverter implements Converter<Calendar, Long> {
        private CalendarToLongConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Long convert(Calendar source) {
            return Long.valueOf(source.getTimeInMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$LongToDateConverter.class */
    public static class LongToDateConverter implements Converter<Long, Date> {
        private LongToDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Date convert(Long source) {
            return new Date(source.longValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateFormatterRegistrar$LongToCalendarConverter.class */
    public static class LongToCalendarConverter implements Converter<Long, Calendar> {
        private LongToCalendarConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Calendar convert(Long source) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(source.longValue());
            return calendar;
        }
    }
}
