package org.springframework.boot.convert;

import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/StringToDataSizeConverter.class */
public final class StringToDataSizeConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, DataSize.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        return convert(source.toString(), getDataUnit(targetType));
    }

    private DataUnit getDataUnit(TypeDescriptor targetType) {
        DataSizeUnit annotation = (DataSizeUnit) targetType.getAnnotation(DataSizeUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private DataSize convert(String source, DataUnit unit) {
        return DataSize.parse(source, unit);
    }
}
