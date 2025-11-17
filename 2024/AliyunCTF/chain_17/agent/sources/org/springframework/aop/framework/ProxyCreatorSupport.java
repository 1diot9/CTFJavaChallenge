package org.springframework.aop.framework;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ProxyCreatorSupport.class */
public class ProxyCreatorSupport extends AdvisedSupport {
    private AopProxyFactory aopProxyFactory;
    private final List<AdvisedSupportListener> listeners;
    private boolean active;

    public ProxyCreatorSupport() {
        this.listeners = new ArrayList();
        this.active = false;
        this.aopProxyFactory = DefaultAopProxyFactory.INSTANCE;
    }

    public ProxyCreatorSupport(AopProxyFactory aopProxyFactory) {
        this.listeners = new ArrayList();
        this.active = false;
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void addListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.add(listener);
    }

    public void removeListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final synchronized AopProxy createAopProxy() {
        if (!this.active) {
            activate();
        }
        return getAopProxyFactory().createAopProxy(this);
    }

    private void activate() {
        this.active = true;
        for (AdvisedSupportListener listener : this.listeners) {
            listener.activated(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.AdvisedSupport
    public void adviceChanged() {
        super.adviceChanged();
        synchronized (this) {
            if (this.active) {
                for (AdvisedSupportListener listener : this.listeners) {
                    listener.adviceChanged(this);
                }
            }
        }
    }

    protected final synchronized boolean isActive() {
        return this.active;
    }
}
