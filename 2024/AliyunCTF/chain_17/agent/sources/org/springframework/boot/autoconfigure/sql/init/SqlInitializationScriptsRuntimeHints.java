package org.springframework.boot.autoconfigure.sql.init;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlInitializationScriptsRuntimeHints.class */
class SqlInitializationScriptsRuntimeHints implements RuntimeHintsRegistrar {
    SqlInitializationScriptsRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("schema.sql").registerPattern("schema-*.sql");
        hints.resources().registerPattern("data.sql").registerPattern("data-*.sql");
    }
}
