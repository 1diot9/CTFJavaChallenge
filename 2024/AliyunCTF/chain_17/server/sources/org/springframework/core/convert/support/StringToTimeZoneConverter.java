package org.springframework.core.convert.support;

import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToTimeZoneConverter.class */
class StringToTimeZoneConverter implements Converter<String, TimeZone> {
    @Override // org.springframework.core.convert.converter.Converter
    public TimeZone convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return StringUtils.parseTimeZoneString(source);
    }
}
