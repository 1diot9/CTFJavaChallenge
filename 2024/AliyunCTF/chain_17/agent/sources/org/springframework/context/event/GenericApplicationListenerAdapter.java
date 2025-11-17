package org.springframework.context.event;

import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/GenericApplicationListenerAdapter.class */
public class GenericApplicationListenerAdapter implements GenericApplicationListener {
    private static final Map<Class<?>, ResolvableType> eventTypeCache = new ConcurrentReferenceHashMap();
    private final ApplicationListener<ApplicationEvent> delegate;

    @Nullable
    private final ResolvableType declaredEventType;

    /* JADX WARN: Multi-variable type inference failed */
    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = delegate;
        this.declaredEventType = resolveDeclaredEventType(this.delegate);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType eventType) {
        ApplicationListener<ApplicationEvent> applicationListener = this.delegate;
        if (applicationListener instanceof GenericApplicationListener) {
            GenericApplicationListener gal = (GenericApplicationListener) applicationListener;
            return gal.supportsEventType(eventType);
        }
        ApplicationListener<ApplicationEvent> applicationListener2 = this.delegate;
        if (!(applicationListener2 instanceof SmartApplicationListener)) {
            return this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType);
        }
        SmartApplicationListener smartApplicationListener = (SmartApplicationListener) applicationListener2;
        Class<?> resolve = eventType.resolve();
        return resolve != null && smartApplicationListener.supportsEventType(resolve);
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        ApplicationListener<ApplicationEvent> applicationListener = this.delegate;
        if (applicationListener instanceof SmartApplicationListener) {
            SmartApplicationListener sal = (SmartApplicationListener) applicationListener;
            if (!sal.supportsSourceType(sourceType)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        ApplicationListener<ApplicationEvent> applicationListener = this.delegate;
        if (!(applicationListener instanceof Ordered)) {
            return Integer.MAX_VALUE;
        }
        Ordered ordered = (Ordered) applicationListener;
        return ordered.getOrder();
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public String getListenerId() {
        ApplicationListener<ApplicationEvent> applicationListener = this.delegate;
        if (!(applicationListener instanceof SmartApplicationListener)) {
            return "";
        }
        SmartApplicationListener sal = (SmartApplicationListener) applicationListener;
        return sal.getListenerId();
    }

    @Nullable
    private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
        Class<?> targetClass;
        ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
        if ((declaredEventType == null || declaredEventType.isAssignableFrom(ApplicationEvent.class)) && (targetClass = AopUtils.getTargetClass(listener)) != listener.getClass()) {
            declaredEventType = resolveDeclaredEventType(targetClass);
        }
        return declaredEventType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
        ResolvableType eventType = eventTypeCache.get(listenerType);
        if (eventType == null) {
            eventType = ResolvableType.forClass(listenerType).as(ApplicationListener.class).getGeneric(new int[0]);
            eventTypeCache.put(listenerType, eventType);
        }
        if (eventType != ResolvableType.NONE) {
            return eventType;
        }
        return null;
    }
}
