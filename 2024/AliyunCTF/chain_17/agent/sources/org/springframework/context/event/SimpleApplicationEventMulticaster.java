package org.springframework.context.event;

import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/SimpleApplicationEventMulticaster.class */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    @Nullable
    private Executor taskExecutor;

    @Nullable
    private ErrorHandler errorHandler;

    @Nullable
    private volatile Log lazyLogger;

    public SimpleApplicationEventMulticaster() {
    }

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    public void setTaskExecutor(@Nullable Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Nullable
    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Nullable
    protected ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void multicastEvent(ApplicationEvent event) {
        multicastEvent(event, null);
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : ResolvableType.forInstance(event);
        Executor executor = getTaskExecutor();
        for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
            if (executor != null && listener.supportsAsyncExecution()) {
                executor.execute(() -> {
                    invokeListener(listener, event);
                });
            } else {
                invokeListener(listener, event);
            }
        }
    }

    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = getErrorHandler();
        if (errorHandler != null) {
            try {
                doInvokeListener(listener, event);
                return;
            } catch (Throwable err) {
                errorHandler.handleError(err);
                return;
            }
        }
        doInvokeListener(listener, event);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x003e, code lost:            if (matchesClassCastMessage(r0, r0.getPayload().getClass()) != false) goto L12;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void doInvokeListener(org.springframework.context.ApplicationListener r5, org.springframework.context.ApplicationEvent r6) {
        /*
            r4 = this;
            r0 = r5
            r1 = r6
            r0.onApplicationEvent(r1)     // Catch: java.lang.ClassCastException -> La
            goto L78
        La:
            r7 = move-exception
            r0 = r7
            java.lang.String r0 = r0.getMessage()
            r8 = r0
            r0 = r8
            if (r0 == 0) goto L41
            r0 = r4
            r1 = r8
            r2 = r6
            java.lang.Class r2 = r2.getClass()
            boolean r0 = r0.matchesClassCastMessage(r1, r2)
            if (r0 != 0) goto L41
            r0 = r6
            boolean r0 = r0 instanceof org.springframework.context.PayloadApplicationEvent
            if (r0 == 0) goto L76
            r0 = r6
            org.springframework.context.PayloadApplicationEvent r0 = (org.springframework.context.PayloadApplicationEvent) r0
            r9 = r0
            r0 = r4
            r1 = r8
            r2 = r9
            java.lang.Object r2 = r2.getPayload()
            java.lang.Class r2 = r2.getClass()
            boolean r0 = r0.matchesClassCastMessage(r1, r2)
            if (r0 == 0) goto L76
        L41:
            r0 = r4
            org.apache.commons.logging.Log r0 = r0.lazyLogger
            r10 = r0
            r0 = r10
            if (r0 != 0) goto L5b
            r0 = r4
            java.lang.Class r0 = r0.getClass()
            org.apache.commons.logging.Log r0 = org.apache.commons.logging.LogFactory.getLog(r0)
            r10 = r0
            r0 = r4
            r1 = r10
            r0.lazyLogger = r1
        L5b:
            r0 = r10
            boolean r0 = r0.isTraceEnabled()
            if (r0 == 0) goto L73
            r0 = r10
            r1 = r5
            java.lang.String r1 = "Non-matching event type for listener: " + r1
            r2 = r7
            r0.trace(r1, r2)
        L73:
            goto L78
        L76:
            r0 = r7
            throw r0
        L78:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(org.springframework.context.ApplicationListener, org.springframework.context.ApplicationEvent):void");
    }

    private boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
        if (classCastMessage.startsWith(eventClass.getName()) || classCastMessage.startsWith(eventClass.toString())) {
            return true;
        }
        int moduleSeparatorIndex = classCastMessage.indexOf(47);
        if (moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1)) {
            return true;
        }
        return false;
    }
}
