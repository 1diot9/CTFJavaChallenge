package org.springframework.boot.convert;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/PeriodToStringConverter.class */
public final class PeriodToStringConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Period.class, String.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        return convert((Period) source, getPeriodStyle(sourceType), getPeriodUnit(sourceType));
    }

    private PeriodStyle getPeriodStyle(TypeDescriptor sourceType) {
        PeriodFormat annotation = (PeriodFormat) sourceType.getAnnotation(PeriodFormat.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private String convert(Period source, PeriodStyle style, ChronoUnit unit) {
        return (style != null ? style : PeriodStyle.ISO8601).print(source, unit);
    }

    private ChronoUnit getPeriodUnit(TypeDescriptor sourceType) {
        PeriodUnit annotation = (PeriodUnit) sourceType.getAnnotation(PeriodUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }
}
