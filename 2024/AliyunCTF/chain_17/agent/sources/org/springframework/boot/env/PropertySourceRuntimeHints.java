package org.springframework.boot.env;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/PropertySourceRuntimeHints.class */
class PropertySourceRuntimeHints implements RuntimeHintsRegistrar {
    PropertySourceRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerTypeIfPresent(classLoader, "org.yaml.snakeyaml.Yaml", typeHint -> {
            typeHint.onReachableType(TypeReference.of((Class<?>) YamlPropertySourceLoader.class));
        });
    }
}
