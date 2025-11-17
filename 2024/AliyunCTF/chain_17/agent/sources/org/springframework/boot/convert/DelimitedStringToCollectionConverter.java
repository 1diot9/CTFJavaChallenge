package org.springframework.boot.convert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DelimitedStringToCollectionConverter.class */
public final class DelimitedStringToCollectionConverter implements ConditionalGenericConverter {
    private final ConversionService conversionService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelimitedStringToCollectionConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Collection.class));
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
        Collection<Object> target = createCollection(targetType, elementDescriptor, elements.length);
        Stream<Object> stream = Arrays.stream(elements).map((v0) -> {
            return v0.trim();
        });
        if (elementDescriptor != null) {
            stream = stream.map(element -> {
                return this.conversionService.convert(element, sourceType, elementDescriptor);
            });
        }
        Objects.requireNonNull(target);
        stream.forEach(target::add);
        return target;
    }

    private Collection<Object> createCollection(TypeDescriptor targetType, TypeDescriptor elementDescriptor, int length) {
        return CollectionFactory.createCollection(targetType.getType(), elementDescriptor != null ? elementDescriptor.getType() : null, length);
    }

    private String[] getElements(String source, String delimiter) {
        return StringUtils.delimitedListToStringArray(source, "".equals(delimiter) ? null : delimiter);
    }
}
