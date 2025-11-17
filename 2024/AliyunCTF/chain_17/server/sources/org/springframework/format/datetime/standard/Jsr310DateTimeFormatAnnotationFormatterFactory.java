package org.springframework.format.datetime.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/Jsr310DateTimeFormatAnnotationFormatterFactory.class */
public class Jsr310DateTimeFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateTimeFormat> {
    private static final Set<Class<?>> FIELD_TYPES = Set.of(Instant.class, LocalDate.class, LocalTime.class, LocalDateTime.class, ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class, YearMonth.class, MonthDay.class);

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(DateTimeFormat annotation, Class fieldType) {
        return getParser2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(DateTimeFormat annotation, Class fieldType) {
        return getPrinter2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public final Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /* renamed from: getPrinter, reason: avoid collision after fix types in other method */
    public Printer<?> getPrinter2(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        if (formatter == DateTimeFormatter.ISO_DATE) {
            if (isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            }
        } else if (formatter == DateTimeFormatter.ISO_TIME) {
            if (isLocal(fieldType)) {
                formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            }
        } else if (formatter == DateTimeFormatter.ISO_DATE_TIME && isLocal(fieldType)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return new TemporalAccessorPrinter(formatter);
    }

    /* renamed from: getParser, reason: avoid collision after fix types in other method */
    public Parser<?> getParser2(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        List<String> resolvedFallbackPatterns = new ArrayList<>();
        for (String fallbackPattern : annotation.fallbackPatterns()) {
            String resolvedFallbackPattern = resolveEmbeddedValue(fallbackPattern);
            if (StringUtils.hasLength(resolvedFallbackPattern)) {
                resolvedFallbackPatterns.add(resolvedFallbackPattern);
            }
        }
        return new TemporalAccessorParser(fieldType, formatter, (String[]) resolvedFallbackPatterns.toArray(new String[0]), annotation);
    }

    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStylePattern(style);
        }
        factory.setIso(annotation.iso());
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }

    private boolean isLocal(Class<?> fieldType) {
        return fieldType.getSimpleName().startsWith("Local");
    }
}
