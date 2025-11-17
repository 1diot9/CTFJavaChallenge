package org.springframework.boot.convert;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DelimitedStringToArrayConverter.class */
public final class DelimitedStringToArrayConverter implements ConditionalGenericConverter {
    private final ConversionService conversionService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelimitedStringToArrayConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object[].class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return targetType.getElementTypeDescriptor() == null || this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((String) source, sourceType, targetType);
    }

    private Object convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Delimiter delimiter = (Delimiter) targetType.getAnnotation(Delimiter.class);
        String[] elements = getElements(source, delimiter != null ? delimiter.value() : ",");
        TypeDescriptor elementDescriptor = targetType.getElementTypeDescriptor();
        Object target = Array.newInstance(elementDescriptor.getType(), elements.length);
        for (int i = 0; i < elements.length; i++) {
            String sourceElement = elements[i];
            Object targetElement = this.conversionService.convert(sourceElement.trim(), sourceType, elementDescriptor);
            Array.set(target, i, targetElement);
        }
        return target;
    }

    private String[] getElements(String source, String delimiter) {
        return StringUtils.delimitedListToStringArray(source, "".equals(delimiter) ? null : delimiter);
    }
}
