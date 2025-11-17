package org.springframework.core.convert.converter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/converter/ConverterRegistry.class */
public interface ConverterRegistry {
    void addConverter(Converter<?, ?> converter);

    <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter);

    void addConverter(GenericConverter converter);

    void addConverterFactory(ConverterFactory<?, ?> factory);

    void removeConvertible(Class<?> sourceType, Class<?> targetType);
}
