package org.springframework.http.converter.json;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.http.ProblemDetail;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/ProblemDetailRuntimeHints.class */
class ProblemDetailRuntimeHints implements RuntimeHintsRegistrar {
    ProblemDetailRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();
        bindingRegistrar.registerReflectionHints(hints.reflection(), ProblemDetail.class);
        if (ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader)) {
            bindingRegistrar.registerReflectionHints(hints.reflection(), ProblemDetailJacksonXmlMixin.class);
        } else if (ClassUtils.isPresent("com.fasterxml.jackson.annotation.JacksonAnnotation", classLoader)) {
            bindingRegistrar.registerReflectionHints(hints.reflection(), ProblemDetailJacksonMixin.class);
        }
    }
}
