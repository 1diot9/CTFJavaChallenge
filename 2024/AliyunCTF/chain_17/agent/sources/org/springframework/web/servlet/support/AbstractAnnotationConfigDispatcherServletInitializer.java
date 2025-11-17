package org.springframework.web.servlet.support;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/AbstractAnnotationConfigDispatcherServletInitializer.class */
public abstract class AbstractAnnotationConfigDispatcherServletInitializer extends AbstractDispatcherServletInitializer {
    @Nullable
    protected abstract Class<?>[] getRootConfigClasses();

    @Nullable
    protected abstract Class<?>[] getServletConfigClasses();

    @Override // org.springframework.web.context.AbstractContextLoaderInitializer
    @Nullable
    protected WebApplicationContext createRootApplicationContext() {
        Class<?>[] configClasses = getRootConfigClasses();
        if (!ObjectUtils.isEmpty((Object[]) configClasses)) {
            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register(configClasses);
            return context;
        }
        return null;
    }

    @Override // org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        Class<?>[] configClasses = getServletConfigClasses();
        if (!ObjectUtils.isEmpty((Object[]) configClasses)) {
            context.register(configClasses);
        }
        return context;
    }
}
