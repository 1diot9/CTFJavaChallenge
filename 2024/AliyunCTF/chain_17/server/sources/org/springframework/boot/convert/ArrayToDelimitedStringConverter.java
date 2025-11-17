package org.springframework.boot.convert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/ArrayToDelimitedStringConverter.class */
public final class ArrayToDelimitedStringConverter implements ConditionalGenericConverter {
    private final CollectionToDelimitedStringConverter delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayToDelimitedStringConverter(ConversionService conversionService) {
        this.delegate = new CollectionToDelimitedStringConverter(conversionService);
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, String.class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.delegate.matches(sourceType, targetType);
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        List<Object> list = Arrays.asList(ObjectUtils.toObjectArray(source));
        return this.delegate.convert((Object) list, sourceType, targetType);
    }
}
