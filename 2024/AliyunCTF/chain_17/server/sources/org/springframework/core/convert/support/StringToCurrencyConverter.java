package org.springframework.core.convert.support;

import java.util.Currency;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToCurrencyConverter.class */
class StringToCurrencyConverter implements Converter<String, Currency> {
    @Override // org.springframework.core.convert.converter.Converter
    public Currency convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Currency.getInstance(source);
    }
}
