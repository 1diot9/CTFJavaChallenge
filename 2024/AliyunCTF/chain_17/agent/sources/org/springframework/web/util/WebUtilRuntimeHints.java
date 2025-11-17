package org.springframework.web.util;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/WebUtilRuntimeHints.class */
class WebUtilRuntimeHints implements RuntimeHintsRegistrar {
    WebUtilRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerResource(new ClassPathResource("HtmlCharacterEntityReferences.properties", getClass()));
    }
}
