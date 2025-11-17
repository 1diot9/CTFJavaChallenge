package org.springframework.boot.autoconfigure.template;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/TemplateRuntimeHints.class */
class TemplateRuntimeHints implements RuntimeHintsRegistrar {
    TemplateRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPatternIfPresent(classLoader, "templates", hint -> {
            hint.includes("templates/*");
        });
    }
}
