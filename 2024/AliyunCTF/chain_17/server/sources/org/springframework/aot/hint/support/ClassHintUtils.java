package org.springframework.aot.hint.support;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeHint;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/ClassHintUtils.class */
public abstract class ClassHintUtils {
    private static final Consumer<TypeHint.Builder> asClassBasedProxy = hint -> {
        hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.DECLARED_FIELDS);
    };
    private static final Consumer<TypeHint.Builder> asProxiedUserClass = hint -> {
        hint.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_METHODS);
    };

    public static void registerProxyIfNecessary(Class<?> candidateClass, RuntimeHints runtimeHints) {
        if (Proxy.isProxyClass(candidateClass)) {
            runtimeHints.proxies().registerJdkProxy(candidateClass.getInterfaces());
            return;
        }
        Class<?> userClass = ClassUtils.getUserClass(candidateClass);
        if (userClass != candidateClass) {
            runtimeHints.reflection().registerType(candidateClass, asClassBasedProxy).registerType(userClass, asProxiedUserClass);
        }
    }
}
