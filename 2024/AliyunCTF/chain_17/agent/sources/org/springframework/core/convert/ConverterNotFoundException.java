package org.springframework.core.convert;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/ConverterNotFoundException.class */
public class ConverterNotFoundException extends ConversionException {

    @Nullable
    private final TypeDescriptor sourceType;
    private final TypeDescriptor targetType;

    public ConverterNotFoundException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        super("No converter found capable of converting from type [" + sourceType + "] to type [" + targetType + "]");
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    @Nullable
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }

    public TypeDescriptor getTargetType() {
        return this.targetType;
    }
}
