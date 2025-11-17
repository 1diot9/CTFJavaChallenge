package org.springframework.boot.convert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/DurationToNumberConverter.class */
public final class DurationToNumberConverter implements GenericConverter {
    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Duration.class, Number.class));
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((Duration) source, getDurationUnit(sourceType), targetType.getObjectType());
    }

    private ChronoUnit getDurationUnit(TypeDescriptor sourceType) {
        DurationUnit annotation = (DurationUnit) sourceType.getAnnotation(DurationUnit.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    private Object convert(Duration source, ChronoUnit unit, Class<?> type) {
        try {
            return type.getConstructor(String.class).newInstance(String.valueOf(DurationStyle.Unit.fromChronoUnit(unit).longValue(source)));
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
            throw new IllegalStateException(ex);
        }
    }
}
