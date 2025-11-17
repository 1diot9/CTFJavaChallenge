package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/SourceFilteringListener.class */
public class SourceFilteringListener implements GenericApplicationListener {
    private final Object source;

    @Nullable
    private GenericApplicationListener delegate;

    public SourceFilteringListener(Object source, ApplicationListener<?> delegate) {
        GenericApplicationListener genericApplicationListenerAdapter;
        this.source = source;
        if (delegate instanceof GenericApplicationListener) {
            GenericApplicationListener gal = (GenericApplicationListener) delegate;
            genericApplicationListenerAdapter = gal;
        } else {
            genericApplicationListenerAdapter = new GenericApplicationListenerAdapter(delegate);
        }
        this.delegate = genericApplicationListenerAdapter;
    }

    protected SourceFilteringListener(Object source) {
        this.source = source;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event.getSource() == this.source) {
            onApplicationEventInternal(event);
        }
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType eventType) {
        return this.delegate == null || this.delegate.supportsEventType(eventType);
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return sourceType != null && sourceType.isInstance(this.source);
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        if (this.delegate != null) {
            return this.delegate.getOrder();
        }
        return Integer.MAX_VALUE;
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public String getListenerId() {
        return this.delegate != null ? this.delegate.getListenerId() : "";
    }

    protected void onApplicationEventInternal(ApplicationEvent event) {
        if (this.delegate == null) {
            throw new IllegalStateException("Must specify a delegate object or override the onApplicationEventInternal method");
        }
        this.delegate.onApplicationEvent(event);
    }
}
