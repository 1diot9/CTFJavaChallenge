package org.springframework.context.event;

import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/GenericApplicationListenerDelegate.class */
class GenericApplicationListenerDelegate<E extends ApplicationEvent> implements GenericApplicationListener {
    private final Class<E> supportedEventType;
    private final Consumer<E> consumer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenericApplicationListenerDelegate(Class<E> supportedEventType, Consumer<E> consumer) {
        this.supportedEventType = supportedEventType;
        this.consumer = consumer;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        this.consumer.accept(this.supportedEventType.cast(applicationEvent));
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType eventType) {
        return this.supportedEventType.isAssignableFrom(eventType.toClass());
    }
}
