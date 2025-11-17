package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/DelegatingApplicationListener.class */
public class DelegatingApplicationListener implements ApplicationListener<ApplicationEvent>, Ordered {
    private static final String PROPERTY_NAME = "context.listener.classes";
    private int order = 0;
    private SimpleApplicationEventMulticaster multicaster;

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent preparedEvent = (ApplicationEnvironmentPreparedEvent) event;
            List<ApplicationListener<ApplicationEvent>> delegates = getListeners(preparedEvent.getEnvironment());
            if (delegates.isEmpty()) {
                return;
            }
            this.multicaster = new SimpleApplicationEventMulticaster();
            for (ApplicationListener<ApplicationEvent> listener : delegates) {
                this.multicaster.addApplicationListener(listener);
            }
        }
        if (this.multicaster != null) {
            this.multicaster.multicastEvent(event);
        }
    }

    private List<ApplicationListener<ApplicationEvent>> getListeners(ConfigurableEnvironment environment) {
        if (environment == null) {
            return Collections.emptyList();
        }
        String classNames = environment.getProperty(PROPERTY_NAME);
        List<ApplicationListener<ApplicationEvent>> listeners = new ArrayList<>();
        if (StringUtils.hasLength(classNames)) {
            for (String className : StringUtils.commaDelimitedListToSet(classNames)) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
                    Assert.isAssignable((Class<?>) ApplicationListener.class, clazz, (Supplier<String>) () -> {
                        return "class [" + className + "] must implement ApplicationListener";
                    });
                    listeners.add((ApplicationListener) BeanUtils.instantiateClass(clazz));
                } catch (Exception ex) {
                    throw new ApplicationContextException("Failed to load context listener class [" + className + "]", ex);
                }
            }
        }
        AnnotationAwareOrderComparator.sort(listeners);
        return listeners;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }
}
