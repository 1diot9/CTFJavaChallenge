package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextAttributeFactoryBean.class */
public class ServletContextAttributeFactoryBean implements FactoryBean<Object>, ServletContextAware {

    @Nullable
    private String attributeName;

    @Nullable
    private Object attribute;

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        if (this.attributeName == null) {
            throw new IllegalArgumentException("Property 'attributeName' is required");
        }
        this.attribute = servletContext.getAttribute(this.attributeName);
        if (this.attribute == null) {
            throw new IllegalStateException("No ServletContext attribute '" + this.attributeName + "' found");
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() throws Exception {
        return this.attribute;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        if (this.attribute != null) {
            return this.attribute.getClass();
        }
        return null;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
