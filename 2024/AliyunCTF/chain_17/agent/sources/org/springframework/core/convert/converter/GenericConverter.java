package org.springframework.core.convert.converter;

import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/converter/GenericConverter.class */
public interface GenericConverter {
    @Nullable
    Set<ConvertiblePair> getConvertibleTypes();

    @Nullable
    Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/converter/GenericConverter$ConvertiblePair.class */
    public static final class ConvertiblePair {
        private final Class<?> sourceType;
        private final Class<?> targetType;

        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            Assert.notNull(sourceType, "Source type must not be null");
            Assert.notNull(targetType, "Target type must not be null");
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return this.sourceType;
        }

        public Class<?> getTargetType() {
            return this.targetType;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || other.getClass() != ConvertiblePair.class) {
                return false;
            }
            ConvertiblePair otherPair = (ConvertiblePair) other;
            return this.sourceType == otherPair.sourceType && this.targetType == otherPair.targetType;
        }

        public int hashCode() {
            return (this.sourceType.hashCode() * 31) + this.targetType.hashCode();
        }

        public String toString() {
            return this.sourceType.getName() + " -> " + this.targetType.getName();
        }
    }
}
