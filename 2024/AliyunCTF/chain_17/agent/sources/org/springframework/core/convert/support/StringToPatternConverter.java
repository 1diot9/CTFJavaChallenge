package org.springframework.core.convert.support;

import java.util.regex.Pattern;
import org.springframework.core.convert.converter.Converter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToPatternConverter.class */
final class StringToPatternConverter implements Converter<String, Pattern> {
    @Override // org.springframework.core.convert.converter.Converter
    public Pattern convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return Pattern.compile(source);
    }
}
