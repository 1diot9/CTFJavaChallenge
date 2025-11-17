package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/SessionScope.class */
public class SessionScope extends AbstractRequestAttributesScope {
    @Override // org.springframework.web.context.request.AbstractRequestAttributesScope
    protected int getScope() {
        return 1;
    }

    @Override // org.springframework.beans.factory.config.Scope
    public String getConversationId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    @Override // org.springframework.web.context.request.AbstractRequestAttributesScope, org.springframework.beans.factory.config.Scope
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object obj;
        Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (mutex) {
            obj = super.get(name, objectFactory);
        }
        return obj;
    }

    @Override // org.springframework.web.context.request.AbstractRequestAttributesScope, org.springframework.beans.factory.config.Scope
    @Nullable
    public Object remove(String name) {
        Object remove;
        Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (mutex) {
            remove = super.remove(name);
        }
        return remove;
    }
}
