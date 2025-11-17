package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/Mergeable.class */
public interface Mergeable {
    boolean isMergeEnabled();

    Object merge(@Nullable Object parent);
}
