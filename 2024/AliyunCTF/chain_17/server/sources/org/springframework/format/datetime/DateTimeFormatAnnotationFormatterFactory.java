package org.springframework.format.datetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/DateTimeFormatAnnotationFormatterFactory.class */
public class DateTimeFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateTimeFormat> {
    private static final Set<Class<?>> FIELD_TYPES = Set.of(Date.class, Calendar.class, Long.class);

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(DateTimeFormat annotation, Class fieldType) {
        return getParser2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(DateTimeFormat annotation, Class fieldType) {
        return getPrinter2(annotation, (Class<?>) fieldType);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /* renamed from: getPrinter, reason: avoid collision after fix types in other method */
    public Printer<?> getPrinter2(DateTimeFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    /* renamed from: getParser, reason: avoid collision after fix types in other method */
    public Parser<?> getParser2(DateTimeFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation, fieldType);
    }

    protected Formatter<Date> getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        DateFormatter formatter = new DateFormatter();
        formatter.setSource(annotation);
        formatter.setIso(annotation.iso());
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            formatter.setStylePattern(style);
        }
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            formatter.setPattern(pattern);
        }
        List<String> resolvedFallbackPatterns = new ArrayList<>();
        for (String fallbackPattern : annotation.fallbackPatterns()) {
            String resolvedFallbackPattern = resolveEmbeddedValue(fallbackPattern);
            if (StringUtils.hasLength(resolvedFallbackPattern)) {
                resolvedFallbackPatterns.add(resolvedFallbackPattern);
            }
        }
        if (!resolvedFallbackPatterns.isEmpty()) {
            formatter.setFallbackPatterns((String[]) resolvedFallbackPatterns.toArray(new String[0]));
        }
        return formatter;
    }
}
