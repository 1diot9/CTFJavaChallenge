package org.springframework.boot.convert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/StringToDurationConverter.class */
public final class StringToDurationConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Duration.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        return convert(source.toString(), getStyle(targetType), getDurationUnit(targetType));
    }

    private DurationStyle getStyle(TypeDescriptor targetType) {
        DurationFormat annotation = (DurationFormat) targetType.getAnnotation(DurationFormat.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private ChronoUnit getDurationUnit(TypeDescriptor targetType) {
        DurationUnit annotation = (DurationUnit) targetType.getAnnotation(DurationUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private Duration convert(String source, DurationStyle style, ChronoUnit unit) {
        return (style != null ? style : DurationStyle.detect(source)).parse(source, unit);
    }
}
