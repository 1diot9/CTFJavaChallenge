package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiObjectTargetSource.class */
public class JndiObjectTargetSource extends JndiObjectLocator implements TargetSource {
    private boolean lookupOnStartup = true;
    private boolean cache = true;

    @Nullable
    private Object cachedObject;

    @Nullable
    private Class<?> targetClass;

    public void setLookupOnStartup(boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override // org.springframework.jndi.JndiObjectLocator, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupOnStartup) {
            Object object = lookup();
            if (this.cache) {
                this.cachedObject = object;
            } else {
                this.targetClass = object.getClass();
            }
        }
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        if (this.cachedObject != null) {
            return this.cachedObject.getClass();
        }
        if (this.targetClass != null) {
            return this.targetClass;
        }
        return getExpectedType();
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return this.cachedObject != null;
    }

    @Override // org.springframework.aop.TargetSource
    @Nullable
    public Object getTarget() {
        Object obj;
        try {
            if (this.lookupOnStartup || !this.cache) {
                return this.cachedObject != null ? this.cachedObject : lookup();
            }
            synchronized (this) {
                if (this.cachedObject == null) {
                    this.cachedObject = lookup();
                }
                obj = this.cachedObject;
            }
            return obj;
        } catch (NamingException ex) {
            throw new JndiLookupFailureException("JndiObjectTargetSource failed to obtain new target object", ex);
        }
    }
}
