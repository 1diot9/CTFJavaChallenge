package org.springframework.boot.context.config;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataPropertiesRuntimeHints.class */
class ConfigDataPropertiesRuntimeHints implements RuntimeHintsRegistrar {
    ConfigDataPropertiesRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        BindableRuntimeHintsRegistrar.forTypes((Class<?>[]) new Class[]{ConfigDataProperties.class}).registerHints(hints);
        hints.reflection().registerMethod(ReflectionUtils.findMethod(ConfigDataLocation.class, "of", String.class), ExecutableMode.INVOKE);
    }
}
