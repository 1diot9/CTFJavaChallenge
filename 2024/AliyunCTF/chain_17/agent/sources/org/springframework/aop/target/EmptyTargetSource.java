package org.springframework.aop.target;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/EmptyTargetSource.class */
public final class EmptyTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 3680494563553489691L;
    public static final EmptyTargetSource INSTANCE = new EmptyTargetSource(null, true);

    @Nullable
    private final Class<?> targetClass;
    private final boolean isStatic;

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass) {
        return forClass(targetClass, true);
    }

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass, boolean isStatic) {
        return (targetClass == null && isStatic) ? INSTANCE : new EmptyTargetSource(targetClass, isStatic);
    }

    private EmptyTargetSource(@Nullable Class<?> targetClass, boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override // org.springframework.aop.TargetSource
    @Nullable
    public Object getTarget() {
        return null;
    }

    private Object readResolve() {
        return (this.targetClass == null && this.isStatic) ? INSTANCE : this;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof EmptyTargetSource) {
                EmptyTargetSource that = (EmptyTargetSource) other;
                if (!ObjectUtils.nullSafeEquals(this.targetClass, that.targetClass) || this.isStatic != that.isStatic) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(getClass(), this.targetClass);
    }

    public String toString() {
        return "EmptyTargetSource: " + (this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") + ", " + (this.isStatic ? "static" : "dynamic");
    }
}
