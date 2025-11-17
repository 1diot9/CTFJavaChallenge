package org.springframework.http;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpMimeTypesRuntimeHints.class */
class HttpMimeTypesRuntimeHints implements RuntimeHintsRegistrar {
    HttpMimeTypesRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.resources().registerPattern("org/springframework/http/mime.types");
    }
}
