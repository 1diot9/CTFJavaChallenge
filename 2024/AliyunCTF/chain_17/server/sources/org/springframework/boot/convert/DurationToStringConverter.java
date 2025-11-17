package org.springframework.boot.convert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DurationToStringConverter.class */
public final class DurationToStringConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Duration.class, String.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((Duration) source, getDurationStyle(sourceType), getDurationUnit(sourceType));
    }

    private ChronoUnit getDurationUnit(TypeDescriptor sourceType) {
        DurationUnit annotation = (DurationUnit) sourceType.getAnnotation(DurationUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private DurationStyle getDurationStyle(TypeDescriptor sourceType) {
        DurationFormat annotation = (DurationFormat) sourceType.getAnnotation(DurationFormat.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private String convert(Duration source, DurationStyle style, ChronoUnit unit) {
        return (style != null ? style : DurationStyle.ISO8601).print(source, unit);
    }
}
