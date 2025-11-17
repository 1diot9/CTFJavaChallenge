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
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.springframework.format.Parser;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/TemporalAccessorParser.class */
public final class TemporalAccessorParser implements Parser<TemporalAccessor> {
    private final Class<? extends TemporalAccessor> temporalAccessorType;
    private final DateTimeFormatter formatter;

    @Nullable
    private final String[] fallbackPatterns;

    @Nullable
    private final Object source;

    public TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter) {
        this(temporalAccessorType, formatter, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter, @Nullable String[] fallbackPatterns, @Nullable Object source) {
        this.temporalAccessorType = temporalAccessorType;
        this.formatter = formatter;
        this.fallbackPatterns = fallbackPatterns;
        this.source = source;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    /* JADX WARN: Removed duplicated region for block: B:23:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x007e  */
    @Override // org.springframework.format.Parser
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.time.temporal.TemporalAccessor parse(java.lang.String r9, java.util.Locale r10) throws java.text.ParseException {
        /*
            r8 = this;
            r0 = r8
            r1 = r9
            r2 = r10
            r3 = r8
            java.time.format.DateTimeFormatter r3 = r3.formatter     // Catch: java.time.format.DateTimeParseException -> Lb
            java.time.temporal.TemporalAccessor r0 = r0.doParse(r1, r2, r3)     // Catch: java.time.format.DateTimeParseException -> Lb
            return r0
        Lb:
            r11 = move-exception
            r0 = r8
            java.lang.String[] r0 = r0.fallbackPatterns
            boolean r0 = org.springframework.util.ObjectUtils.isEmpty(r0)
            if (r0 != 0) goto L4d
            r0 = r8
            java.lang.String[] r0 = r0.fallbackPatterns
            r12 = r0
            r0 = r12
            int r0 = r0.length
            r13 = r0
            r0 = 0
            r14 = r0
        L24:
            r0 = r14
            r1 = r13
            if (r0 >= r1) goto L4a
            r0 = r12
            r1 = r14
            r0 = r0[r1]
            r15 = r0
            r0 = r15
            java.time.format.DateTimeFormatter r0 = org.springframework.format.datetime.standard.DateTimeFormatterUtils.createStrictDateTimeFormatter(r0)     // Catch: java.time.format.DateTimeParseException -> L42
            r16 = r0
            r0 = r8
            r1 = r9
            r2 = r10
            r3 = r16
            java.time.temporal.TemporalAccessor r0 = r0.doParse(r1, r2, r3)     // Catch: java.time.format.DateTimeParseException -> L42
            return r0
        L42:
            r16 = move-exception
            int r14 = r14 + 1
            goto L24
        L4a:
            goto L55
        L4d:
            r0 = r8
            r1 = r9
            java.time.temporal.TemporalAccessor r0 = r0.defaultParse(r1)     // Catch: java.time.format.DateTimeParseException -> L53
            return r0
        L53:
            r12 = move-exception
        L55:
            r0 = r8
            java.lang.Object r0 = r0.source
            if (r0 == 0) goto L7e
            java.time.format.DateTimeParseException r0 = new java.time.format.DateTimeParseException
            r1 = r0
            java.lang.String r2 = "Unable to parse date time value \"%s\" using configuration from %s"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = r3
            r5 = 0
            r6 = r9
            r4[r5] = r6
            r4 = r3
            r5 = 1
            r6 = r8
            java.lang.Object r6 = r6.source
            r4[r5] = r6
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r3 = r9
            r4 = r11
            int r4 = r4.getErrorIndex()
            r5 = r11
            r1.<init>(r2, r3, r4, r5)
            throw r0
        L7e:
            r0 = r11
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.format.datetime.standard.TemporalAccessorParser.parse(java.lang.String, java.util.Locale):java.time.temporal.TemporalAccessor");
    }

    private TemporalAccessor doParse(String text, Locale locale, DateTimeFormatter formatter) throws DateTimeParseException {
        DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(formatter, locale);
        if (Instant.class == this.temporalAccessorType) {
            return (TemporalAccessor) formatterToUse.parse(text, Instant::from);
        }
        if (LocalDate.class == this.temporalAccessorType) {
            return LocalDate.parse(text, formatterToUse);
        }
        if (LocalTime.class == this.temporalAccessorType) {
            return LocalTime.parse(text, formatterToUse);
        }
        if (LocalDateTime.class == this.temporalAccessorType) {
            return LocalDateTime.parse(text, formatterToUse);
        }
        if (ZonedDateTime.class == this.temporalAccessorType) {
            return ZonedDateTime.parse(text, formatterToUse);
        }
        if (OffsetDateTime.class == this.temporalAccessorType) {
            return OffsetDateTime.parse(text, formatterToUse);
        }
        if (OffsetTime.class == this.temporalAccessorType) {
            return OffsetTime.parse(text, formatterToUse);
        }
        if (YearMonth.class == this.temporalAccessorType) {
            return YearMonth.parse(text, formatterToUse);
        }
        if (MonthDay.class == this.temporalAccessorType) {
            return MonthDay.parse(text, formatterToUse);
        }
        throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
    }

    private TemporalAccessor defaultParse(String text) throws DateTimeParseException {
        if (Instant.class == this.temporalAccessorType) {
            return Instant.parse(text);
        }
        if (LocalDate.class == this.temporalAccessorType) {
            return LocalDate.parse(text);
        }
        if (LocalTime.class == this.temporalAccessorType) {
            return LocalTime.parse(text);
        }
        if (LocalDateTime.class == this.temporalAccessorType) {
            return LocalDateTime.parse(text);
        }
        if (ZonedDateTime.class == this.temporalAccessorType) {
            return ZonedDateTime.parse(text);
        }
        if (OffsetDateTime.class == this.temporalAccessorType) {
            return OffsetDateTime.parse(text);
        }
        if (OffsetTime.class == this.temporalAccessorType) {
            return OffsetTime.parse(text);
        }
        if (YearMonth.class == this.temporalAccessorType) {
            return YearMonth.parse(text);
        }
        if (MonthDay.class == this.temporalAccessorType) {
            return MonthDay.parse(text);
        }
        throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
    }
}
