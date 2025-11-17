package org.springframework.boot.autoconfigure.web.servlet;

import java.util.Collection;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.DispatcherServlet;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletRegistrationBean.class */
public class DispatcherServletRegistrationBean extends ServletRegistrationBean<DispatcherServlet> implements DispatcherServletPath {
    private final String path;

    public DispatcherServletRegistrationBean(DispatcherServlet servlet, String path) {
        super(servlet, new String[0]);
        Assert.notNull(path, "Path must not be null");
        this.path = path;
        super.addUrlMappings(getServletUrlMapping());
    }

    @Override // org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath
    public String getPath() {
        return this.path;
    }

    @Override // org.springframework.boot.web.servlet.ServletRegistrationBean
    public void setUrlMappings(Collection<String> urlMappings) {
        throw new UnsupportedOperationException("URL Mapping cannot be changed on a DispatcherServlet registration");
    }

    @Override // org.springframework.boot.web.servlet.ServletRegistrationBean
    public void addUrlMappings(String... urlMappings) {
        throw new UnsupportedOperationException("URL Mapping cannot be changed on a DispatcherServlet registration");
    }
}
