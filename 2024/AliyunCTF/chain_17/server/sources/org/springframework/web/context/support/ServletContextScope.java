package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextScope.class */
public class ServletContextScope implements Scope, DisposableBean {
    private final ServletContext servletContext;
    private final Map<String, Runnable> destructionCallbacks = new LinkedHashMap();

    public ServletContextScope(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        this.servletContext = servletContext;
    }

    @Override // org.springframework.beans.factory.config.Scope
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            this.servletContext.setAttribute(name, scopedObject);
        }
        return scopedObject;
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public Object remove(String name) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject != null) {
            synchronized (this.destructionCallbacks) {
                this.destructionCallbacks.remove(name);
            }
            this.servletContext.removeAttribute(name);
            return scopedObject;
        }
        return null;
    }

    @Override // org.springframework.beans.factory.config.Scope
    public void registerDestructionCallback(String name, Runnable callback) {
        synchronized (this.destructionCallbacks) {
            this.destructionCallbacks.put(name, callback);
        }
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public String getConversationId() {
        return null;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        synchronized (this.destructionCallbacks) {
            for (Runnable runnable : this.destructionCallbacks.values()) {
                runnable.run();
            }
            this.destructionCallbacks.clear();
        }
    }
}
