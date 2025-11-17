package org.springframework.boot.security.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/security/servlet/ApplicationContextRequestMatcher.class */
public abstract class ApplicationContextRequestMatcher<C> implements RequestMatcher {
    private final Class<? extends C> contextClass;
    private volatile boolean initialized;
    private final Object initializeLock = new Object();

    protected abstract boolean matches(HttpServletRequest request, Supplier<C> context);

    public ApplicationContextRequestMatcher(Class<? extends C> contextClass) {
        Assert.notNull(contextClass, "Context class must not be null");
        this.contextClass = contextClass;
    }

    public final boolean matches(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        if (ignoreApplicationContext(webApplicationContext)) {
            return false;
        }
        Supplier<C> context = () -> {
            return getContext(webApplicationContext);
        };
        if (!this.initialized) {
            synchronized (this.initializeLock) {
                if (!this.initialized) {
                    initialized(context);
                    this.initialized = true;
                }
            }
        }
        return matches(request, context);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private C getContext(WebApplicationContext webApplicationContext) {
        if (this.contextClass.isInstance(webApplicationContext)) {
            return webApplicationContext;
        }
        return (C) webApplicationContext.getBean(this.contextClass);
    }

    protected boolean ignoreApplicationContext(WebApplicationContext webApplicationContext) {
        return false;
    }

    protected void initialized(Supplier<C> context) {
    }
}
