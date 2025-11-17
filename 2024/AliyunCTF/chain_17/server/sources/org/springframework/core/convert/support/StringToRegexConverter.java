package org.springframework.core.convert.support;

import kotlin.text.Regex;
import org.springframework.core.convert.converter.Converter;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToRegexConverter.class */
class StringToRegexConverter implements Converter<String, Regex> {
    @Override // org.springframework.core.convert.converter.Converter
    public Regex convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return new Regex(source);
    }
}
