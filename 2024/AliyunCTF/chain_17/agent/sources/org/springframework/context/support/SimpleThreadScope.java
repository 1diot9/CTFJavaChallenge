package org.springframework.context.support;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/SimpleThreadScope.class */
public class SimpleThreadScope implements Scope {
    private static final Log logger = LogFactory.getLog((Class<?>) SimpleThreadScope.class);
    private final ThreadLocal<Map<String, Object>> threadScope = NamedThreadLocal.withInitial("SimpleThreadScope", HashMap::new);

    @Override // org.springframework.beans.factory.config.Scope
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = this.threadScope.get();
        Object scopedObject = scope.get(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            scope.put(name, scopedObject);
        }
        return scopedObject;
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public Object remove(String name) {
        Map<String, Object> scope = this.threadScope.get();
        return scope.remove(name);
    }

    @Override // org.springframework.beans.factory.config.Scope
    public void registerDestructionCallback(String name, Runnable callback) {
        logger.warn("SimpleThreadScope does not support destruction callbacks. Consider using RequestScope in a web environment.");
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override // org.springframework.beans.factory.config.Scope
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}
