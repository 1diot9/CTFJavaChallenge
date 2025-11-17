package org.springframework.boot.web.servlet;

import jakarta.servlet.ServletException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/DelegatingFilterProxyRegistrationBean.class */
public class DelegatingFilterProxyRegistrationBean extends AbstractFilterRegistrationBean<DelegatingFilterProxy> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final String targetBeanName;

    public DelegatingFilterProxyRegistrationBean(String targetBeanName, ServletRegistrationBean<?>... servletRegistrationBeans) {
        super(servletRegistrationBeans);
        Assert.hasLength(targetBeanName, "TargetBeanName must not be null or empty");
        this.targetBeanName = targetBeanName;
        setName(targetBeanName);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getTargetBeanName() {
        return this.targetBeanName;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.web.servlet.AbstractFilterRegistrationBean
    public DelegatingFilterProxy getFilter() {
        return new DelegatingFilterProxy(this.targetBeanName, getWebApplicationContext()) { // from class: org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean.1
            @Override // org.springframework.web.filter.DelegatingFilterProxy, org.springframework.web.filter.GenericFilterBean
            protected void initFilterBean() throws ServletException {
            }
        };
    }

    private WebApplicationContext getWebApplicationContext() {
        Assert.notNull(this.applicationContext, "ApplicationContext be injected");
        Assert.isInstanceOf(WebApplicationContext.class, this.applicationContext);
        return (WebApplicationContext) this.applicationContext;
    }
}
