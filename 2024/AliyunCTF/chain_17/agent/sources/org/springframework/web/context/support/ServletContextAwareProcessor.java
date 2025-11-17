package org.springframework.web.context.support;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextAwareProcessor.class */
public class ServletContextAwareProcessor implements BeanPostProcessor {

    @Nullable
    private ServletContext servletContext;

    @Nullable
    private ServletConfig servletConfig;

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletContextAwareProcessor() {
    }

    public ServletContextAwareProcessor(ServletContext servletContext) {
        this(servletContext, null);
    }

    public ServletContextAwareProcessor(ServletConfig servletConfig) {
        this(null, servletConfig);
    }

    public ServletContextAwareProcessor(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        this.servletContext = servletContext;
        this.servletConfig = servletConfig;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public ServletContext getServletContext() {
        if (this.servletContext == null && getServletConfig() != null) {
            return getServletConfig().getServletContext();
        }
        return this.servletContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (getServletContext() != null && (bean instanceof ServletContextAware)) {
            ServletContextAware servletContextAware = (ServletContextAware) bean;
            servletContextAware.setServletContext(getServletContext());
        }
        if (getServletConfig() != null && (bean instanceof ServletConfigAware)) {
            ServletConfigAware servletConfigAware = (ServletConfigAware) bean;
            servletConfigAware.setServletConfig(getServletConfig());
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
