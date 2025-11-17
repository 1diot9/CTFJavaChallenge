package org.springframework.context;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ApplicationEventPublisher.class */
public interface ApplicationEventPublisher {
    void publishEvent(Object event);

    default void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }
}
