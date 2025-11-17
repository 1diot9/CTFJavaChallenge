package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/SmartApplicationListener.class */
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType);

    default boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return true;
    }

    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    default String getListenerId() {
        return "";
    }
}
