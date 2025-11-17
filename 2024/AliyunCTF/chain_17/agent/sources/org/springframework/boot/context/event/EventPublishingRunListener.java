package org.springframework.boot.context.event;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ErrorHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/EventPublishingRunListener.class */
class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {
    private final SpringApplication application;
    private final String[] args;
    private final SimpleApplicationEventMulticaster initialMulticaster = new SimpleApplicationEventMulticaster();

    EventPublishingRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        multicastInitialEvent(new ApplicationStartingEvent(bootstrapContext, this.application, this.args));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        multicastInitialEvent(new ApplicationEnvironmentPreparedEvent(bootstrapContext, this.application, this.args, environment));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void contextPrepared(ConfigurableApplicationContext context) {
        multicastInitialEvent(new ApplicationContextInitializedEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void contextLoaded(ConfigurableApplicationContext context) {
        for (ApplicationListener<?> listener : this.application.getListeners()) {
            if (listener instanceof ApplicationContextAware) {
                ApplicationContextAware contextAware = (ApplicationContextAware) listener;
                contextAware.setApplicationContext(context);
            }
            context.addApplicationListener(listener);
        }
        multicastInitialEvent(new ApplicationPreparedEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        context.publishEvent((ApplicationEvent) new ApplicationStartedEvent(this.application, this.args, context, timeTaken));
        AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        context.publishEvent((ApplicationEvent) new ApplicationReadyEvent(this.application, this.args, context, timeTaken));
        AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        ApplicationFailedEvent event = new ApplicationFailedEvent(this.application, this.args, context, exception);
        if (context != null && context.isActive()) {
            context.publishEvent((ApplicationEvent) event);
            return;
        }
        if (context instanceof AbstractApplicationContext) {
            AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) context;
            for (ApplicationListener<?> listener : abstractApplicationContext.getApplicationListeners()) {
                this.initialMulticaster.addApplicationListener(listener);
            }
        }
        this.initialMulticaster.setErrorHandler(new LoggingErrorHandler());
        this.initialMulticaster.multicastEvent(event);
    }

    private void multicastInitialEvent(ApplicationEvent event) {
        refreshApplicationListeners();
        this.initialMulticaster.multicastEvent(event);
    }

    private void refreshApplicationListeners() {
        Set<ApplicationListener<?>> listeners = this.application.getListeners();
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = this.initialMulticaster;
        Objects.requireNonNull(simpleApplicationEventMulticaster);
        listeners.forEach(simpleApplicationEventMulticaster::addApplicationListener);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/event/EventPublishingRunListener$LoggingErrorHandler.class */
    private static final class LoggingErrorHandler implements ErrorHandler {
        private static final Log logger = LogFactory.getLog((Class<?>) EventPublishingRunListener.class);

        private LoggingErrorHandler() {
        }

        @Override // org.springframework.util.ErrorHandler
        public void handleError(Throwable throwable) {
            logger.warn("Error calling ApplicationEventListener", throwable);
        }
    }
}
