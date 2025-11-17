package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextParameterFactoryBean.class */
public class ServletContextParameterFactoryBean implements FactoryBean<String>, ServletContextAware {

    @Nullable
    private String initParamName;

    @Nullable
    private String paramValue;

    public void setInitParamName(String initParamName) {
        this.initParamName = initParamName;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        if (this.initParamName == null) {
            throw new IllegalArgumentException("initParamName is required");
        }
        this.paramValue = servletContext.getInitParameter(this.initParamName);
        if (this.paramValue == null) {
            throw new IllegalStateException("No ServletContext init parameter '" + this.initParamName + "' found");
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public String getObject() {
        return this.paramValue;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<String> getObjectType() {
        return String.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
