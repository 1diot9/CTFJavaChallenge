package org.springframework.core.convert.support;

import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToLocaleConverter.class */
final class StringToLocaleConverter implements Converter<String, Locale> {
    @Override // org.springframework.core.convert.converter.Converter
    @Nullable
    public Locale convert(String source) {
        return StringUtils.parseLocale(source);
    }
}
