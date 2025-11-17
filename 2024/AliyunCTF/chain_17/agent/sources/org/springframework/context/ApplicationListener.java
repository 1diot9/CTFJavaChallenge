package org.springframework.context;

import java.util.EventListener;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ApplicationListener.class */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);

    default boolean supportsAsyncExecution() {
        return true;
    }

    static <T> ApplicationListener<PayloadApplicationEvent<T>> forPayload(Consumer<T> consumer) {
        return event -> {
            consumer.accept(event.getPayload());
        };
    }
}
