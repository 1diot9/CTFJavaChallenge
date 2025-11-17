package org.springframework.boot.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/CollectionToDelimitedStringConverter.class */
public final class CollectionToDelimitedStringConverter implements ConditionalGenericConverter {
    private final ConversionService conversionService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CollectionToDelimitedStringConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, String.class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        TypeDescriptor sourceElementType = sourceType.getElementTypeDescriptor();
        return targetType == null || sourceElementType == null || this.conversionService.canConvert(sourceElementType, targetType) || sourceElementType.getType().isAssignableFrom(targetType.getType());
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Collection<?> sourceCollection = (Collection) source;
        return convert(sourceCollection, sourceType, targetType);
    }

    private Object convert(Collection<?> source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source.isEmpty()) {
            return "";
        }
        return source.stream().map(element -> {
            return convertElement(element, sourceType, targetType);
        }).collect(Collectors.joining(getDelimiter(sourceType)));
    }

    private CharSequence getDelimiter(TypeDescriptor sourceType) {
        Delimiter annotation = (Delimiter) sourceType.getAnnotation(Delimiter.class);
        return annotation != null ? annotation.value() : ",";
    }

    private String convertElement(Object element, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.valueOf(this.conversionService.convert(element, sourceType.elementTypeDescriptor(element), targetType));
    }
}
