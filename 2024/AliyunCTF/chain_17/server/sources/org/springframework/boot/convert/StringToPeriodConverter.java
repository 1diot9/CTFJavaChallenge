package org.springframework.boot.convert;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/StringToPeriodConverter.class */
public final class StringToPeriodConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Period.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        return convert(source.toString(), getStyle(targetType), getPeriodUnit(targetType));
    }

    private PeriodStyle getStyle(TypeDescriptor targetType) {
        PeriodFormat annotation = (PeriodFormat) targetType.getAnnotation(PeriodFormat.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private ChronoUnit getPeriodUnit(TypeDescriptor targetType) {
        PeriodUnit annotation = (PeriodUnit) targetType.getAnnotation(PeriodUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private Period convert(String source, PeriodStyle style, ChronoUnit unit) {
        return (style != null ? style : PeriodStyle.detect(source)).parse(source, unit);
    }
}
