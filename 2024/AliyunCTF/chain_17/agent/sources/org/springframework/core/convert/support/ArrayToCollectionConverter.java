package org.springframework.core.convert.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/ArrayToCollectionConverter.class */
public final class ArrayToCollectionConverter implements ConditionalGenericConverter {
    private final ConversionService conversionService;

    public ArrayToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Collection.class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        int length = Array.getLength(source);
        TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
        Collection<Object> target = createCollection(targetType.getType(), elementDesc != null ? elementDesc.getType() : null, length);
        if (elementDesc == null) {
            for (int i = 0; i < length; i++) {
                target.add(Array.get(source, i));
            }
        } else {
            for (int i2 = 0; i2 < length; i2++) {
                Object sourceElement = Array.get(source, i2);
                Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), elementDesc);
                target.add(targetElement);
            }
        }
        return target;
    }

    private Collection<Object> createCollection(Class<?> targetType, @Nullable Class<?> elementType, int length) {
        if (targetType.isInterface() && targetType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList(length);
        }
        return CollectionFactory.createCollection(targetType, elementType, length);
    }
}
