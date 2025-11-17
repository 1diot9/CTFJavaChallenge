package org.springframework.boot.builder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/builder/ParentContextApplicationContextInitializer.class */
public class ParentContextApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private int order = Integer.MIN_VALUE;
    private final ApplicationContext parent;

    public ParentContextApplicationContextInitializer(ApplicationContext parent) {
        this.parent = parent;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext != this.parent) {
            applicationContext.setParent(this.parent);
            applicationContext.addApplicationListener(EventPublisher.INSTANCE);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/builder/ParentContextApplicationContextInitializer$EventPublisher.class */
    private static final class EventPublisher implements ApplicationListener<ContextRefreshedEvent>, Ordered {
        private static final EventPublisher INSTANCE = new EventPublisher();

        private EventPublisher() {
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            ApplicationContext context = event.getApplicationContext();
            if ((context instanceof ConfigurableApplicationContext) && context == event.getSource()) {
                context.publishEvent((ApplicationEvent) new ParentContextAvailableEvent((ConfigurableApplicationContext) context));
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/builder/ParentContextApplicationContextInitializer$ParentContextAvailableEvent.class */
    public static class ParentContextAvailableEvent extends ApplicationEvent {
        public ParentContextAvailableEvent(ConfigurableApplicationContext applicationContext) {
            super(applicationContext);
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return (ConfigurableApplicationContext) getSource();
        }
    }
}
