package org.springframework.aot.hint;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ExecutableMode.class */
public enum ExecutableMode {
    INTROSPECT,
    INVOKE;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean includes(@Nullable ExecutableMode other) {
        return other == null || ordinal() >= other.ordinal();
    }
}
