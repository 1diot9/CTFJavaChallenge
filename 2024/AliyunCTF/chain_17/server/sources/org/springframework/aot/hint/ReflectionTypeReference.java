package org.springframework.aot.hint;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ReflectionTypeReference.class */
public final class ReflectionTypeReference extends AbstractTypeReference {
    private final Class<?> type;

    private ReflectionTypeReference(Class<?> type) {
        super(type.getPackageName(), type.getSimpleName(), getEnclosingClass(type));
        this.type = type;
    }

    @Nullable
    private static TypeReference getEnclosingClass(Class<?> type) {
        Class<?> candidate = type.isArray() ? type.componentType().getEnclosingClass() : type.getEnclosingClass();
        if (candidate != null) {
            return new ReflectionTypeReference(candidate);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ReflectionTypeReference of(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        Assert.notNull(type.getCanonicalName(), "'type.getCanonicalName()' must not be null");
        return new ReflectionTypeReference(type);
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getCanonicalName() {
        return this.type.getCanonicalName();
    }

    @Override // org.springframework.aot.hint.AbstractTypeReference
    protected boolean isPrimitive() {
        return this.type.isPrimitive() || (this.type.isArray() && this.type.componentType().isPrimitive());
    }
}
