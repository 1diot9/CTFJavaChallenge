package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToCharsetConverter.class */
class StringToCharsetConverter implements Converter<String, Charset> {
    @Override // org.springframework.core.convert.converter.Converter
    public Charset convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Charset.forName(source);
    }
}
