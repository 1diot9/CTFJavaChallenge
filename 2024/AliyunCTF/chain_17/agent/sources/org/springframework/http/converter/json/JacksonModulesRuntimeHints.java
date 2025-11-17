package org.springframework.http.converter.json;

import java.util.function.Consumer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/JacksonModulesRuntimeHints.class */
class JacksonModulesRuntimeHints implements RuntimeHintsRegistrar {
    private static final Consumer<TypeHint.Builder> asJacksonModule = builder -> {
        builder.onReachableType(Jackson2ObjectMapperBuilder.class).withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    };

    JacksonModulesRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerTypeIfPresent(classLoader, "com.fasterxml.jackson.datatype.jdk8.Jdk8Module", asJacksonModule).registerTypeIfPresent(classLoader, "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", asJacksonModule).registerTypeIfPresent(classLoader, "com.fasterxml.jackson.module.kotlin.KotlinModule", asJacksonModule);
    }
}
