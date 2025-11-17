package org.springframework.context.expression;

import java.lang.reflect.AnnotatedElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/AnnotatedElementKey.class */
public final class AnnotatedElementKey implements Comparable<AnnotatedElementKey> {
    private final AnnotatedElement element;

    @Nullable
    private final Class<?> targetClass;

    public AnnotatedElementKey(AnnotatedElement element, @Nullable Class<?> targetClass) {
        Assert.notNull(element, "AnnotatedElement must not be null");
        this.element = element;
        this.targetClass = targetClass;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AnnotatedElementKey) {
                AnnotatedElementKey that = (AnnotatedElementKey) other;
                if (!this.element.equals(that.element) || !ObjectUtils.nullSafeEquals(this.targetClass, that.targetClass)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.element.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
    }

    public String toString() {
        return this.element + (this.targetClass != null ? " on " + this.targetClass : "");
    }

    @Override // java.lang.Comparable
    public int compareTo(AnnotatedElementKey other) {
        int result = this.element.toString().compareTo(other.element.toString());
        if (result == 0 && this.targetClass != null) {
            if (other.targetClass == null) {
                return 1;
            }
            result = this.targetClass.getName().compareTo(other.targetClass.getName());
        }
        return result;
    }
}
