package org.springframework.boot.convert;

import java.time.Period;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/NumberToPeriodConverter.class */
public final class NumberToPeriodConverter implements GenericConverter {
    private final StringToPeriodConverter delegate = new StringToPeriodConverter();

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Number.class, Period.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.delegate.convert(source != null ? source.toString() : null, TypeDescriptor.valueOf(String.class), targetType);
    }
}
