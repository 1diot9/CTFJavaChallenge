package org.springframework.context.event;

import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/GenericApplicationListener.class */
public interface GenericApplicationListener extends SmartApplicationListener {
    boolean supportsEventType(ResolvableType eventType);

    @Override // org.springframework.context.event.SmartApplicationListener
    default boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return supportsEventType(ResolvableType.forClass(eventType));
    }

    static <E extends ApplicationEvent> GenericApplicationListener forEventType(Class<E> eventType, Consumer<E> consumer) {
        return new GenericApplicationListenerDelegate(eventType, consumer);
    }
}
