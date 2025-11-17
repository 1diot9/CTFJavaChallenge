package org.springframework.core;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodClassKey.class */
public final class MethodClassKey implements Comparable<MethodClassKey> {
    private final Method method;

    @Nullable
    private final Class<?> targetClass;

    public MethodClassKey(Method method, @Nullable Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MethodClassKey) {
                MethodClassKey that = (MethodClassKey) other;
                if (!this.method.equals(that.method) || !ObjectUtils.nullSafeEquals(this.targetClass, that.targetClass)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.method.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
    }

    public String toString() {
        return this.method + (this.targetClass != null ? " on " + this.targetClass : "");
    }

    @Override // java.lang.Comparable
    public int compareTo(MethodClassKey other) {
        int result = this.method.getName().compareTo(other.method.getName());
        if (result == 0) {
            result = this.method.toString().compareTo(other.method.toString());
            if (result == 0 && this.targetClass != null && other.targetClass != null) {
                result = this.targetClass.getName().compareTo(other.targetClass.getName());
            }
        }
        return result;
    }
}
