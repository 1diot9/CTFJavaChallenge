package org.springframework.boot.web.servlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/ServletRegistrationBean.class */
public class ServletRegistrationBean<T extends Servlet> extends DynamicRegistrationBean<ServletRegistration.Dynamic> {
    private static final String[] DEFAULT_MAPPINGS = {"/*"};
    private T servlet;
    private Set<String> urlMappings;
    private boolean alwaysMapUrl;
    private int loadOnStartup;
    private MultipartConfigElement multipartConfig;

    public ServletRegistrationBean() {
        this.urlMappings = new LinkedHashSet();
        this.alwaysMapUrl = true;
        this.loadOnStartup = -1;
    }

    public ServletRegistrationBean(T servlet, String... urlMappings) {
        this(servlet, true, urlMappings);
    }

    public ServletRegistrationBean(T servlet, boolean alwaysMapUrl, String... urlMappings) {
        this.urlMappings = new LinkedHashSet();
        this.alwaysMapUrl = true;
        this.loadOnStartup = -1;
        Assert.notNull(servlet, "Servlet must not be null");
        Assert.notNull(urlMappings, "UrlMappings must not be null");
        this.servlet = servlet;
        this.alwaysMapUrl = alwaysMapUrl;
        this.urlMappings.addAll(Arrays.asList(urlMappings));
    }

    public void setServlet(T servlet) {
        Assert.notNull(servlet, "Servlet must not be null");
        this.servlet = servlet;
    }

    public T getServlet() {
        return this.servlet;
    }

    public void setUrlMappings(Collection<String> urlMappings) {
        Assert.notNull(urlMappings, "UrlMappings must not be null");
        this.urlMappings = new LinkedHashSet(urlMappings);
    }

    public Collection<String> getUrlMappings() {
        return this.urlMappings;
    }

    public void addUrlMappings(String... urlMappings) {
        Assert.notNull(urlMappings, "UrlMappings must not be null");
        this.urlMappings.addAll(Arrays.asList(urlMappings));
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.multipartConfig = multipartConfig;
    }

    public MultipartConfigElement getMultipartConfig() {
        return this.multipartConfig;
    }

    @Override // org.springframework.boot.web.servlet.RegistrationBean
    protected String getDescription() {
        Assert.notNull(this.servlet, "Servlet must not be null");
        return "servlet " + getServletName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public ServletRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        String name = getServletName();
        return servletContext.addServlet(name, this.servlet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public void configure(ServletRegistration.Dynamic registration) {
        super.configure((ServletRegistrationBean<T>) registration);
        String[] urlMapping = StringUtils.toStringArray(this.urlMappings);
        if (urlMapping.length == 0 && this.alwaysMapUrl) {
            urlMapping = DEFAULT_MAPPINGS;
        }
        if (!ObjectUtils.isEmpty((Object[]) urlMapping)) {
            registration.addMapping(urlMapping);
        }
        registration.setLoadOnStartup(this.loadOnStartup);
        if (this.multipartConfig != null) {
            registration.setMultipartConfig(this.multipartConfig);
        }
    }

    public String getServletName() {
        return getOrDeduceName(this.servlet);
    }

    public String toString() {
        return getServletName() + " urls=" + getUrlMappings();
    }
}
