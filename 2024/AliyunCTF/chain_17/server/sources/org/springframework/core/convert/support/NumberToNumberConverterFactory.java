package org.springframework.core.convert.support;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/NumberToNumberConverterFactory.class */
final class NumberToNumberConverterFactory implements ConverterFactory<Number, Number>, ConditionalConverter {
    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Number> Converter<Number, T> getConverter(Class<T> targetType) {
        return new NumberToNumber(targetType);
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return !sourceType.equals(targetType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/NumberToNumberConverterFactory$NumberToNumber.class */
    private static final class NumberToNumber<T extends Number> implements Converter<Number, T> {
        private final Class<T> targetType;

        NumberToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        public T convert(Number number) {
            return (T) NumberUtils.convertNumberToTargetClass(number, this.targetType);
        }
    }
}
