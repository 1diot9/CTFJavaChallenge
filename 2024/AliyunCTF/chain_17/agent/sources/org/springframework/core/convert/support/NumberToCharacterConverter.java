package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/NumberToCharacterConverter.class */
final class NumberToCharacterConverter implements Converter<Number, Character> {
    @Override // org.springframework.core.convert.converter.Converter
    public Character convert(Number source) {
        return Character.valueOf((char) source.shortValue());
    }
}
