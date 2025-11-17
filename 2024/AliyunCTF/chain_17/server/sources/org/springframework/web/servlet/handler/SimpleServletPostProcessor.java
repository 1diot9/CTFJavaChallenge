package org.springframework.web.servlet.handler;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/SimpleServletPostProcessor.class */
public class SimpleServletPostProcessor implements DestructionAwareBeanPostProcessor, ServletContextAware, ServletConfigAware {
    private boolean useSharedServletConfig = true;

    @Nullable
    private ServletContext servletContext;

    @Nullable
    private ServletConfig servletConfig;

    public void setUseSharedServletConfig(boolean useSharedServletConfig) {
        this.useSharedServletConfig = useSharedServletConfig;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.web.context.ServletConfigAware
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Servlet) {
            Servlet servlet = (Servlet) bean;
            ServletConfig config = this.servletConfig;
            if (config == null || !this.useSharedServletConfig) {
                config = new DelegatingServletConfig(beanName, this.servletContext);
            }
            try {
                servlet.init(config);
            } catch (ServletException ex) {
                throw new BeanInitializationException("Servlet.init threw exception", ex);
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof Servlet) {
            Servlet servlet = (Servlet) bean;
            servlet.destroy();
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        return bean instanceof Servlet;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/SimpleServletPostProcessor$DelegatingServletConfig.class */
    private static class DelegatingServletConfig implements ServletConfig {
        private final String servletName;

        @Nullable
        private final ServletContext servletContext;

        public DelegatingServletConfig(String servletName, @Nullable ServletContext servletContext) {
            this.servletName = servletName;
            this.servletContext = servletContext;
        }

        @Override // jakarta.servlet.ServletConfig
        public String getServletName() {
            return this.servletName;
        }

        @Override // jakarta.servlet.ServletConfig
        @Nullable
        public ServletContext getServletContext() {
            return this.servletContext;
        }

        @Override // jakarta.servlet.ServletConfig
        @Nullable
        public String getInitParameter(String paramName) {
            return null;
        }

        @Override // jakarta.servlet.ServletConfig
        public Enumeration<String> getInitParameterNames() {
            return Collections.emptyEnumeration();
        }
    }
}
