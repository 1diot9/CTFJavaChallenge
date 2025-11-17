package org.springframework.core.convert.support;

import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/ConversionServiceFactory.class */
public final class ConversionServiceFactory {
    private ConversionServiceFactory() {
    }

    public static void registerConverters(@Nullable Set<?> converters, ConverterRegistry registry) {
        if (converters != null) {
            for (Object candidate : converters) {
                if (candidate instanceof GenericConverter) {
                    GenericConverter genericConverter = (GenericConverter) candidate;
                    registry.addConverter(genericConverter);
                } else if (candidate instanceof Converter) {
                    Converter<?, ?> converter = (Converter) candidate;
                    registry.addConverter(converter);
                } else if (candidate instanceof ConverterFactory) {
                    ConverterFactory<?, ?> converterFactory = (ConverterFactory) candidate;
                    registry.addConverterFactory(converterFactory);
                } else {
                    throw new IllegalArgumentException("Each converter object must implement one of the Converter, ConverterFactory, or GenericConverter interfaces");
                }
            }
        }
    }
}
