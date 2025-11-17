package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/LogbackRuntimeHints.class */
class LogbackRuntimeHints implements RuntimeHintsRegistrar {
    LogbackRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        if (!ClassUtils.isPresent("ch.qos.logback.classic.LoggerContext", classLoader)) {
            return;
        }
        ReflectionHints reflection = hints.reflection();
        registerHintsForLogbackLoggingSystemTypeChecks(reflection, classLoader);
        registerHintsForBuiltInLogbackConverters(reflection);
        registerHintsForSpringBootConverters(reflection);
    }

    private void registerHintsForLogbackLoggingSystemTypeChecks(ReflectionHints reflection, ClassLoader classLoader) {
        reflection.registerType(LoggerContext.class, new MemberCategory[0]);
        reflection.registerTypeIfPresent(classLoader, "org.slf4j.bridge.SLF4JBridgeHandler", typeHint -> {
        });
    }

    private void registerHintsForBuiltInLogbackConverters(ReflectionHints reflection) {
        registerForPublicConstructorInvocation(reflection, DateTokenConverter.class, IntegerTokenConverter.class, SyslogStartConverter.class);
    }

    private void registerHintsForSpringBootConverters(ReflectionHints reflection) {
        registerForPublicConstructorInvocation(reflection, ColorConverter.class, ExtendedWhitespaceThrowableProxyConverter.class, WhitespaceThrowableProxyConverter.class, CorrelationIdConverter.class);
    }

    private void registerForPublicConstructorInvocation(ReflectionHints reflection, Class<?>... classes) {
        reflection.registerTypes(TypeReference.listOf(classes), hint -> {
            hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        });
    }
}
