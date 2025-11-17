package org.springframework.boot;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationRunListeners.class */
public class SpringApplicationRunListeners {
    private final Log log;
    private final List<SpringApplicationRunListener> listeners;
    private final ApplicationStartup applicationStartup;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringApplicationRunListeners(Log log, List<SpringApplicationRunListener> listeners, ApplicationStartup applicationStartup) {
        this.log = log;
        this.listeners = List.copyOf(listeners);
        this.applicationStartup = applicationStartup;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void starting(ConfigurableBootstrapContext bootstrapContext, Class<?> mainApplicationClass) {
        doWithListeners("spring.boot.application.starting", listener -> {
            listener.starting(bootstrapContext);
        }, step -> {
            if (mainApplicationClass != null) {
                step.tag("mainApplicationClass", mainApplicationClass.getName());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        doWithListeners("spring.boot.application.environment-prepared", listener -> {
            listener.environmentPrepared(bootstrapContext, environment);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void contextPrepared(ConfigurableApplicationContext context) {
        doWithListeners("spring.boot.application.context-prepared", listener -> {
            listener.contextPrepared(context);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void contextLoaded(ConfigurableApplicationContext context) {
        doWithListeners("spring.boot.application.context-loaded", listener -> {
            listener.contextLoaded(context);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        doWithListeners("spring.boot.application.started", listener -> {
            listener.started(context, timeTaken);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        doWithListeners("spring.boot.application.ready", listener -> {
            listener.ready(context, timeTaken);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        doWithListeners("spring.boot.application.failed", listener -> {
            callFailedListener(listener, context, exception);
        }, step -> {
            step.tag(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE, exception.getClass().toString());
            step.tag(JsonEncoder.MESSAGE_ATTR_NAME, exception.getMessage());
        });
    }

    private void callFailedListener(SpringApplicationRunListener listener, ConfigurableApplicationContext context, Throwable exception) {
        try {
            listener.failed(context, exception);
        } catch (Throwable ex) {
            if (exception == null) {
                ReflectionUtils.rethrowRuntimeException(ex);
            }
            if (this.log.isDebugEnabled()) {
                this.log.error("Error handling failed", ex);
            } else {
                String message = ex.getMessage();
                this.log.warn("Error handling failed (" + (message != null ? message : "no error message") + ")");
            }
        }
    }

    private void doWithListeners(String stepName, Consumer<SpringApplicationRunListener> listenerAction) {
        doWithListeners(stepName, listenerAction, null);
    }

    private void doWithListeners(String stepName, Consumer<SpringApplicationRunListener> listenerAction, Consumer<StartupStep> stepAction) {
        StartupStep step = this.applicationStartup.start(stepName);
        this.listeners.forEach(listenerAction);
        if (stepAction != null) {
            stepAction.accept(step);
        }
        step.end();
    }
}
