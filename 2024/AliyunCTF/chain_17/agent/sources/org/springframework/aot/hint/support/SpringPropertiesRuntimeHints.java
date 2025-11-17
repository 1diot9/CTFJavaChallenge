package org.springframework.aot.hint.support;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/SpringPropertiesRuntimeHints.class */
class SpringPropertiesRuntimeHints implements RuntimeHintsRegistrar {
    SpringPropertiesRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("spring.properties");
    }
}
