package org.springframework.aot.hint.support;

import ch.qos.logback.core.CoreConstants;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/ObjectToObjectConverterRuntimeHints.class */
class ObjectToObjectConverterRuntimeHints implements RuntimeHintsRegistrar {
    ObjectToObjectConverterRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();
        TypeReference sqlDateTypeReference = TypeReference.of("java.sql.Date");
        reflectionHints.registerTypeIfPresent(classLoader, sqlDateTypeReference.getName(), hint -> {
            hint.withMethod("toLocalDate", Collections.emptyList(), ExecutableMode.INVOKE).onReachableType(sqlDateTypeReference).withMethod(CoreConstants.VALUE_OF, List.of(TypeReference.of((Class<?>) LocalDate.class)), ExecutableMode.INVOKE).onReachableType(sqlDateTypeReference);
        });
        reflectionHints.registerTypeIfPresent(classLoader, "org.springframework.http.HttpMethod", builder -> {
            builder.withMethod(CoreConstants.VALUE_OF, List.of(TypeReference.of((Class<?>) String.class)), ExecutableMode.INVOKE);
        });
        reflectionHints.registerTypeIfPresent(classLoader, "java.net.URI", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    }
}
