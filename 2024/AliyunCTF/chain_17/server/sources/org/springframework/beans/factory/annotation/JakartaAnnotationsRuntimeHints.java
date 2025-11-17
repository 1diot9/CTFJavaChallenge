package org.springframework.beans.factory.annotation;

import java.util.stream.Stream;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/JakartaAnnotationsRuntimeHints.class */
class JakartaAnnotationsRuntimeHints implements RuntimeHintsRegistrar {
    JakartaAnnotationsRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        Stream.of((Object[]) new String[]{"jakarta.inject.Inject", "jakarta.inject.Provider", "jakarta.inject.Qualifier"}).forEach(typeName -> {
            hints.reflection().registerType(TypeReference.of(typeName), new MemberCategory[0]);
        });
    }
}
