package org.springframework.aot.hint.support;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/PathMatchingResourcePatternResolverRuntimeHints.class */
class PathMatchingResourcePatternResolverRuntimeHints implements RuntimeHintsRegistrar {
    PathMatchingResourcePatternResolverRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.reflection().registerType(TypeReference.of("org.eclipse.core.runtime.FileLocator"), new MemberCategory[0]);
    }
}
