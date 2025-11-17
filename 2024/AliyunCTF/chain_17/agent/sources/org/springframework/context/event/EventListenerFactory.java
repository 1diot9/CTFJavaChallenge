package org.springframework.context.event;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/EventListenerFactory.class */
public interface EventListenerFactory {
    boolean supportsMethod(Method method);

    ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method);
}
