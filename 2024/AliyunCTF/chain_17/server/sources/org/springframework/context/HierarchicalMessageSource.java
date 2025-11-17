package org.springframework.context;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/HierarchicalMessageSource.class */
public interface HierarchicalMessageSource extends MessageSource {
    void setParentMessageSource(@Nullable MessageSource parent);

    @Nullable
    MessageSource getParentMessageSource();
}
