package org.springframework.context;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/LifecycleProcessor.class */
public interface LifecycleProcessor extends Lifecycle {
    void onRefresh();

    void onClose();
}
