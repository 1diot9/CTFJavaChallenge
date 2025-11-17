package org.springframework.core.convert.support;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.core.convert.converter.Converter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/ZonedDateTimeToCalendarConverter.class */
final class ZonedDateTimeToCalendarConverter implements Converter<ZonedDateTime, Calendar> {
    @Override // org.springframework.core.convert.converter.Converter
    public Calendar convert(ZonedDateTime source) {
        return GregorianCalendar.from(source);
    }
}
