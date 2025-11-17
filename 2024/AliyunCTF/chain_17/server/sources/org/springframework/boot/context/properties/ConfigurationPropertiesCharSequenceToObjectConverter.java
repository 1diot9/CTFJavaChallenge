package org.springframework.boot.context.properties;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesCharSequenceToObjectConverter.class */
final class ConfigurationPropertiesCharSequenceToObjectConverter implements ConditionalGenericConverter {
    private static final TypeDescriptor STRING = TypeDescriptor.valueOf(String.class);
    private static final TypeDescriptor BYTE_ARRAY = TypeDescriptor.valueOf(byte[].class);
    private static final Set<GenericConverter.ConvertiblePair> TYPES = Collections.singleton(new GenericConverter.ConvertiblePair(CharSequence.class, Object.class));
    private final ThreadLocal<Boolean> disable = new ThreadLocal<>();
    private final ConversionService conversionService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationPropertiesCharSequenceToObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return TYPES;
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType() == String.class || this.disable.get() == Boolean.TRUE) {
            return false;
        }
        this.disable.set(Boolean.TRUE);
        try {
            boolean canDirectlyConvertCharSequence = this.conversionService.canConvert(sourceType, targetType);
            if (canDirectlyConvertCharSequence && !isStringConversionBetter(sourceType, targetType)) {
                return false;
            }
            boolean canConvert = this.conversionService.canConvert(STRING, targetType);
            this.disable.remove();
            return canConvert;
        } finally {
            this.disable.remove();
        }
    }

    private boolean isStringConversionBetter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConversionService conversionService = this.conversionService;
        if (conversionService instanceof ApplicationConversionService) {
            ApplicationConversionService applicationConversionService = (ApplicationConversionService) conversionService;
            if (applicationConversionService.isConvertViaObjectSourceType(sourceType, targetType)) {
                return true;
            }
        }
        if ((targetType.isArray() || targetType.isCollection()) && !targetType.equals(BYTE_ARRAY)) {
            return true;
        }
        return false;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.convert(source.toString(), STRING, targetType);
    }
}
