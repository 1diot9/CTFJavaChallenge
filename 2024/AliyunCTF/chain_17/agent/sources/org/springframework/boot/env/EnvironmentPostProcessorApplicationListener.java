package org.springframework.boot.env;

import java.util.List;
import java.util.function.Function;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/EnvironmentPostProcessorApplicationListener.class */
public class EnvironmentPostProcessorApplicationListener implements SmartApplicationListener, Ordered {
    public static final int DEFAULT_ORDER = -2147483638;
    private final DeferredLogs deferredLogs;
    private int order;
    private final Function<ClassLoader, EnvironmentPostProcessorsFactory> postProcessorsFactory;

    public EnvironmentPostProcessorApplicationListener() {
        this(EnvironmentPostProcessorsFactory::fromSpringFactories);
    }

    private EnvironmentPostProcessorApplicationListener(Function<ClassLoader, EnvironmentPostProcessorsFactory> postProcessorsFactory) {
        this.order = -2147483638;
        this.postProcessorsFactory = postProcessorsFactory;
        this.deferredLogs = new DeferredLogs();
    }

    public static EnvironmentPostProcessorApplicationListener with(EnvironmentPostProcessorsFactory postProcessorsFactory) {
        return new EnvironmentPostProcessorApplicationListener(classloader -> {
            return postProcessorsFactory;
        });
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType) || ApplicationPreparedEvent.class.isAssignableFrom(eventType) || ApplicationFailedEvent.class.isAssignableFrom(eventType);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent environmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) event;
            onApplicationEnvironmentPreparedEvent(environmentPreparedEvent);
        }
        if (event instanceof ApplicationPreparedEvent) {
            onApplicationPreparedEvent();
        }
        if (event instanceof ApplicationFailedEvent) {
            onApplicationFailedEvent();
        }
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        SpringApplication application = event.getSpringApplication();
        for (EnvironmentPostProcessor postProcessor : getEnvironmentPostProcessors(application.getResourceLoader(), event.getBootstrapContext())) {
            postProcessor.postProcessEnvironment(environment, application);
        }
    }

    private void onApplicationPreparedEvent() {
        finish();
    }

    private void onApplicationFailedEvent() {
        finish();
    }

    private void finish() {
        this.deferredLogs.switchOverAll();
    }

    List<EnvironmentPostProcessor> getEnvironmentPostProcessors(ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext) {
        ClassLoader classLoader = resourceLoader != null ? resourceLoader.getClassLoader() : null;
        EnvironmentPostProcessorsFactory postProcessorsFactory = this.postProcessorsFactory.apply(classLoader);
        return postProcessorsFactory.getEnvironmentPostProcessors(this.deferredLogs, bootstrapContext);
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
