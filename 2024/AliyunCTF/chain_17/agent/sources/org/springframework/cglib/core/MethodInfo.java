package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/MethodInfo.class */
public abstract class MethodInfo {
    public abstract ClassInfo getClassInfo();

    public abstract int getModifiers();

    public abstract Signature getSignature();

    public abstract Type[] getExceptionTypes();

    public boolean equals(Object o) {
        if (o == null || !(o instanceof MethodInfo)) {
            return false;
        }
        MethodInfo other = (MethodInfo) o;
        return getSignature().equals(other.getSignature());
    }

    public int hashCode() {
        return getSignature().hashCode();
    }

    public String toString() {
        return getSignature().toString();
    }
}
