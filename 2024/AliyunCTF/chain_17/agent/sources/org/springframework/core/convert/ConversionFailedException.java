package org.springframework.core.convert;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/ConversionFailedException.class */
public class ConversionFailedException extends ConversionException {

    @Nullable
    private final TypeDescriptor sourceType;
    private final TypeDescriptor targetType;

    @Nullable
    private final Object value;

    public ConversionFailedException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType, @Nullable Object value, Throwable cause) {
        super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value [" + ObjectUtils.nullSafeConciseToString(value) + "]", cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }

    @Nullable
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }

    public TypeDescriptor getTargetType() {
        return this.targetType;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }
}
