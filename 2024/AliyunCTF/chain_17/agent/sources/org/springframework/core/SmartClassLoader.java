package org.springframework.core;

import java.security.ProtectionDomain;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/SmartClassLoader.class */
public interface SmartClassLoader {
    default boolean isClassReloadable(Class<?> clazz) {
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    default ClassLoader getOriginalClassLoader() {
        return (ClassLoader) this;
    }

    default Class<?> publicDefineClass(String name, byte[] b, @Nullable ProtectionDomain protectionDomain) {
        throw new UnsupportedOperationException();
    }
}
