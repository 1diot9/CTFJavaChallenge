package org.springframework.format.datetime.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters.class */
public final class DateTimeConverters {
    private DateTimeConverters() {
    }

    public static void registerConverters(ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter(new LocalDateTimeToLocalDateConverter());
        registry.addConverter(new LocalDateTimeToLocalTimeConverter());
        registry.addConverter(new ZonedDateTimeToLocalDateConverter());
        registry.addConverter(new ZonedDateTimeToLocalTimeConverter());
        registry.addConverter(new ZonedDateTimeToLocalDateTimeConverter());
        registry.addConverter(new ZonedDateTimeToOffsetDateTimeConverter());
        registry.addConverter(new ZonedDateTimeToInstantConverter());
        registry.addConverter(new OffsetDateTimeToLocalDateConverter());
        registry.addConverter(new OffsetDateTimeToLocalTimeConverter());
        registry.addConverter(new OffsetDateTimeToLocalDateTimeConverter());
        registry.addConverter(new OffsetDateTimeToZonedDateTimeConverter());
        registry.addConverter(new OffsetDateTimeToInstantConverter());
        registry.addConverter(new CalendarToZonedDateTimeConverter());
        registry.addConverter(new CalendarToOffsetDateTimeConverter());
        registry.addConverter(new CalendarToLocalDateConverter());
        registry.addConverter(new CalendarToLocalTimeConverter());
        registry.addConverter(new CalendarToLocalDateTimeConverter());
        registry.addConverter(new CalendarToInstantConverter());
        registry.addConverter(new LongToInstantConverter());
        registry.addConverter(new InstantToLongConverter());
    }

    private static ZonedDateTime calendarToZonedDateTime(Calendar source) {
        if (source instanceof GregorianCalendar) {
            GregorianCalendar gc = (GregorianCalendar) source;
            return gc.toZonedDateTime();
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.getTimeInMillis()), source.getTimeZone().toZoneId());
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$LocalDateTimeToLocalDateConverter.class */
    private static class LocalDateTimeToLocalDateConverter implements Converter<LocalDateTime, LocalDate> {
        private LocalDateTimeToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$LocalDateTimeToLocalTimeConverter.class */
    private static class LocalDateTimeToLocalTimeConverter implements Converter<LocalDateTime, LocalTime> {
        private LocalDateTimeToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(LocalDateTime source) {
            return source.toLocalTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$ZonedDateTimeToLocalDateConverter.class */
    private static class ZonedDateTimeToLocalDateConverter implements Converter<ZonedDateTime, LocalDate> {
        private ZonedDateTimeToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(ZonedDateTime source) {
            return source.toLocalDate();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$ZonedDateTimeToLocalTimeConverter.class */
    private static class ZonedDateTimeToLocalTimeConverter implements Converter<ZonedDateTime, LocalTime> {
        private ZonedDateTimeToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(ZonedDateTime source) {
            return source.toLocalTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$ZonedDateTimeToLocalDateTimeConverter.class */
    private static class ZonedDateTimeToLocalDateTimeConverter implements Converter<ZonedDateTime, LocalDateTime> {
        private ZonedDateTimeToLocalDateTimeConverter() {
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [java.time.LocalDateTime] */
        @Override // org.springframework.core.convert.converter.Converter
        public LocalDateTime convert(ZonedDateTime source) {
            return source.toLocalDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$ZonedDateTimeToOffsetDateTimeConverter.class */
    private static class ZonedDateTimeToOffsetDateTimeConverter implements Converter<ZonedDateTime, OffsetDateTime> {
        private ZonedDateTimeToOffsetDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public OffsetDateTime convert(ZonedDateTime source) {
            return source.toOffsetDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$ZonedDateTimeToInstantConverter.class */
    private static class ZonedDateTimeToInstantConverter implements Converter<ZonedDateTime, Instant> {
        private ZonedDateTimeToInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Instant convert(ZonedDateTime source) {
            return source.toInstant();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$OffsetDateTimeToLocalDateConverter.class */
    private static class OffsetDateTimeToLocalDateConverter implements Converter<OffsetDateTime, LocalDate> {
        private OffsetDateTimeToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(OffsetDateTime source) {
            return source.toLocalDate();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$OffsetDateTimeToLocalTimeConverter.class */
    private static class OffsetDateTimeToLocalTimeConverter implements Converter<OffsetDateTime, LocalTime> {
        private OffsetDateTimeToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(OffsetDateTime source) {
            return source.toLocalTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$OffsetDateTimeToLocalDateTimeConverter.class */
    private static class OffsetDateTimeToLocalDateTimeConverter implements Converter<OffsetDateTime, LocalDateTime> {
        private OffsetDateTimeToLocalDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDateTime convert(OffsetDateTime source) {
            return source.toLocalDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$OffsetDateTimeToZonedDateTimeConverter.class */
    private static class OffsetDateTimeToZonedDateTimeConverter implements Converter<OffsetDateTime, ZonedDateTime> {
        private OffsetDateTimeToZonedDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public ZonedDateTime convert(OffsetDateTime source) {
            return source.toZonedDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$OffsetDateTimeToInstantConverter.class */
    private static class OffsetDateTimeToInstantConverter implements Converter<OffsetDateTime, Instant> {
        private OffsetDateTimeToInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Instant convert(OffsetDateTime source) {
            return source.toInstant();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToZonedDateTimeConverter.class */
    private static class CalendarToZonedDateTimeConverter implements Converter<Calendar, ZonedDateTime> {
        private CalendarToZonedDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public ZonedDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToOffsetDateTimeConverter.class */
    private static class CalendarToOffsetDateTimeConverter implements Converter<Calendar, OffsetDateTime> {
        private CalendarToOffsetDateTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public OffsetDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toOffsetDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToLocalDateConverter.class */
    private static class CalendarToLocalDateConverter implements Converter<Calendar, LocalDate> {
        private CalendarToLocalDateConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalDate convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDate();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToLocalTimeConverter.class */
    private static class CalendarToLocalTimeConverter implements Converter<Calendar, LocalTime> {
        private CalendarToLocalTimeConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public LocalTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToLocalDateTimeConverter.class */
    private static class CalendarToLocalDateTimeConverter implements Converter<Calendar, LocalDateTime> {
        private CalendarToLocalDateTimeConverter() {
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [java.time.LocalDateTime] */
        @Override // org.springframework.core.convert.converter.Converter
        public LocalDateTime convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toLocalDateTime();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$CalendarToInstantConverter.class */
    private static class CalendarToInstantConverter implements Converter<Calendar, Instant> {
        private CalendarToInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Instant convert(Calendar source) {
            return DateTimeConverters.calendarToZonedDateTime(source).toInstant();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$LongToInstantConverter.class */
    private static class LongToInstantConverter implements Converter<Long, Instant> {
        private LongToInstantConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Instant convert(Long source) {
            return Instant.ofEpochMilli(source.longValue());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeConverters$InstantToLongConverter.class */
    private static class InstantToLongConverter implements Converter<Instant, Long> {
        private InstantToLongConverter() {
        }

        @Override // org.springframework.core.convert.converter.Converter
        public Long convert(Instant source) {
            return Long.valueOf(source.toEpochMilli());
        }
    }
}
