package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/TypedValue.class */
public class TypedValue {
    public static final TypedValue NULL = new TypedValue(null);

    @Nullable
    private final Object value;

    @Nullable
    private TypeDescriptor typeDescriptor;

    public TypedValue(@Nullable Object value) {
        this.value = value;
        this.typeDescriptor = null;
    }

    public TypedValue(@Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
        this.value = value;
        this.typeDescriptor = typeDescriptor;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public TypeDescriptor getTypeDescriptor() {
        if (this.typeDescriptor == null && this.value != null) {
            this.typeDescriptor = TypeDescriptor.forObject(this.value);
        }
        return this.typeDescriptor;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof TypedValue) {
                TypedValue that = (TypedValue) other;
                if (!ObjectUtils.nullSafeEquals(this.value, that.value) || ((this.typeDescriptor != null || that.typeDescriptor != null) && !ObjectUtils.nullSafeEquals(getTypeDescriptor(), that.getTypeDescriptor()))) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return "TypedValue: '" + this.value + "' of [" + getTypeDescriptor() + "]";
    }
}
