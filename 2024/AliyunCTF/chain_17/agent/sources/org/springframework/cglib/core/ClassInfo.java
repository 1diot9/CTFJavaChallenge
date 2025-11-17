package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassInfo.class */
public abstract class ClassInfo {
    public abstract Type getType();

    public abstract Type getSuperType();

    public abstract Type[] getInterfaces();

    public abstract int getModifiers();

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ClassInfo)) {
            return false;
        }
        ClassInfo classInfo = (ClassInfo) o;
        return getType().equals(classInfo.getType());
    }

    public int hashCode() {
        return getType().hashCode();
    }

    public String toString() {
        return getType().getClassName();
    }
}
