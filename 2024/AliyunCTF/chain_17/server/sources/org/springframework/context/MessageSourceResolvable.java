package org.springframework.context;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/MessageSourceResolvable.class */
public interface MessageSourceResolvable {
    @Nullable
    String[] getCodes();

    @Nullable
    default Object[] getArguments() {
        return null;
    }

    @Nullable
    default String getDefaultMessage() {
        return null;
    }
}
