package org.springframework.format.support;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionServiceRuntimeHints.class */
class FormattingConversionServiceRuntimeHints implements RuntimeHintsRegistrar {
    FormattingConversionServiceRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(TypeReference.of("javax.money.MonetaryAmount"), new MemberCategory[0]);
    }
}
