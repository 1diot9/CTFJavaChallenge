package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToNumberConverterFactory.class */
final class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber(targetType);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToNumberConverterFactory$StringToNumber.class */
    private static final class StringToNumber<T extends Number> implements Converter<String, T> {
        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        @Nullable
        public T convert(String str) {
            if (str.isEmpty()) {
                return null;
            }
            return (T) NumberUtils.parseNumber(str, this.targetType);
        }
    }
}
