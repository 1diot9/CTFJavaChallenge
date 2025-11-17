package org.springframework.context.event;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/DefaultEventListenerFactory.class */
public class DefaultEventListenerFactory implements EventListenerFactory, Ordered {
    private int order = Integer.MAX_VALUE;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.event.EventListenerFactory
    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override // org.springframework.context.event.EventListenerFactory
    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new ApplicationListenerMethodAdapter(beanName, type, method);
    }
}
