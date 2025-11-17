package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToEnumConverterFactory.class */
final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum(ConversionUtils.getEnumType(targetType));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToEnumConverterFactory$StringToEnum.class */
    private static class StringToEnum<T extends Enum> implements Converter<String, T> {
        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        @Nullable
        public T convert(String str) {
            if (str.isEmpty()) {
                return null;
            }
            return (T) Enum.valueOf(this.enumType, str.trim());
        }
    }
}
