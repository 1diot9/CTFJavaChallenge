package org.springframework.aot.generate;

import org.springframework.aot.hint.AbstractTypeReference;
import org.springframework.javapoet.ClassName;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedTypeReference.class */
public final class GeneratedTypeReference extends AbstractTypeReference {
    private final ClassName className;

    private GeneratedTypeReference(ClassName className) {
        super(className.packageName(), className.simpleName(), safeCreate(className.enclosingClassName()));
        this.className = className;
    }

    @Nullable
    private static GeneratedTypeReference safeCreate(@Nullable ClassName className) {
        if (className != null) {
            return new GeneratedTypeReference(className);
        }
        return null;
    }

    public static GeneratedTypeReference of(ClassName className) {
        Assert.notNull(className, "ClassName must not be null");
        return new GeneratedTypeReference(className);
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getCanonicalName() {
        return this.className.canonicalName();
    }

    @Override // org.springframework.aot.hint.AbstractTypeReference
    protected boolean isPrimitive() {
        return this.className.isPrimitive();
    }
}
