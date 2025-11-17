package org.springframework.boot.web.servlet;

import jakarta.servlet.Registration;
import jakarta.servlet.Registration.Dynamic;
import jakarta.servlet.ServletContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/DynamicRegistrationBean.class */
public abstract class DynamicRegistrationBean<D extends Registration.Dynamic> extends RegistrationBean implements BeanNameAware {
    private static final Log logger = LogFactory.getLog((Class<?>) RegistrationBean.class);
    private String name;
    private boolean asyncSupported = true;
    private Map<String, String> initParameters = new LinkedHashMap();
    private String beanName;
    private boolean ignoreRegistrationFailure;

    protected abstract D addRegistration(String description, ServletContext servletContext);

    public void setName(String name) {
        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        Assert.notNull(initParameters, "InitParameters must not be null");
        this.initParameters = new LinkedHashMap(initParameters);
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull(name, "Name must not be null");
        this.initParameters.put(name, value);
    }

    @Override // org.springframework.boot.web.servlet.RegistrationBean
    protected final void register(String description, ServletContext servletContext) {
        D registration = addRegistration(description, servletContext);
        if (registration == null) {
            if (this.ignoreRegistrationFailure) {
                logger.info(StringUtils.capitalize(description) + " was not registered (possibly already registered?)");
                return;
            }
            throw new IllegalStateException("Failed to register '%s' on the servlet context. Possibly already registered?".formatted(description));
        }
        configure(registration);
    }

    public void setIgnoreRegistrationFailure(boolean ignoreRegistrationFailure) {
        this.ignoreRegistrationFailure = ignoreRegistrationFailure;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void configure(D registration) {
        registration.setAsyncSupported(this.asyncSupported);
        if (!this.initParameters.isEmpty()) {
            registration.setInitParameters(this.initParameters);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getOrDeduceName(Object value) {
        if (this.name != null) {
            return this.name;
        }
        if (this.beanName != null) {
            return this.beanName;
        }
        return Conventions.getVariableName(value);
    }
}
