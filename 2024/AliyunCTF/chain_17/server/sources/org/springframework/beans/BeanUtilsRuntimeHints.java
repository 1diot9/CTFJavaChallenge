package org.springframework.beans;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ResourceEditor;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanUtilsRuntimeHints.class */
class BeanUtilsRuntimeHints implements RuntimeHintsRegistrar {
    BeanUtilsRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();
        reflectionHints.registerType(ResourceEditor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        reflectionHints.registerTypeIfPresent(classLoader, "org.springframework.http.MediaTypeEditor", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    }
}
