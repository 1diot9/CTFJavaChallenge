package org.springframework.boot.web.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/AbstractFilterRegistrationBean.class */
public abstract class AbstractFilterRegistrationBean<T extends Filter> extends DynamicRegistrationBean<FilterRegistration.Dynamic> {
    private static final String[] DEFAULT_URL_MAPPINGS = {"/*"};
    private EnumSet<jakarta.servlet.DispatcherType> dispatcherTypes;
    private Set<ServletRegistrationBean<?>> servletRegistrationBeans = new LinkedHashSet();
    private Set<String> servletNames = new LinkedHashSet();
    private Set<String> urlPatterns = new LinkedHashSet();
    private boolean matchAfter = false;

    public abstract T getFilter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractFilterRegistrationBean(ServletRegistrationBean<?>... servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
    }

    public void setServletRegistrationBeans(Collection<? extends ServletRegistrationBean<?>> servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        this.servletRegistrationBeans = new LinkedHashSet(servletRegistrationBeans);
    }

    public Collection<ServletRegistrationBean<?>> getServletRegistrationBeans() {
        return this.servletRegistrationBeans;
    }

    public void addServletRegistrationBeans(ServletRegistrationBean<?>... servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
    }

    public void setServletNames(Collection<String> servletNames) {
        Assert.notNull(servletNames, "ServletNames must not be null");
        this.servletNames = new LinkedHashSet(servletNames);
    }

    public Collection<String> getServletNames() {
        return this.servletNames;
    }

    public void addServletNames(String... servletNames) {
        Assert.notNull(servletNames, "ServletNames must not be null");
        this.servletNames.addAll(Arrays.asList(servletNames));
    }

    public void setUrlPatterns(Collection<String> urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns = new LinkedHashSet(urlPatterns);
    }

    public Collection<String> getUrlPatterns() {
        return this.urlPatterns;
    }

    public void addUrlPatterns(String... urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        Collections.addAll(this.urlPatterns, urlPatterns);
    }

    public EnumSet<jakarta.servlet.DispatcherType> determineDispatcherTypes() {
        if (this.dispatcherTypes == null) {
            T filter = getFilter();
            if (ClassUtils.isPresent("org.springframework.web.filter.OncePerRequestFilter", filter.getClass().getClassLoader()) && (filter instanceof OncePerRequestFilter)) {
                return EnumSet.allOf(jakarta.servlet.DispatcherType.class);
            }
            return EnumSet.of(jakarta.servlet.DispatcherType.REQUEST);
        }
        return EnumSet.copyOf((EnumSet) this.dispatcherTypes);
    }

    public void setDispatcherTypes(jakarta.servlet.DispatcherType first, jakarta.servlet.DispatcherType... rest) {
        this.dispatcherTypes = EnumSet.of(first, rest);
    }

    public void setDispatcherTypes(EnumSet<jakarta.servlet.DispatcherType> dispatcherTypes) {
        this.dispatcherTypes = dispatcherTypes;
    }

    public void setMatchAfter(boolean matchAfter) {
        this.matchAfter = matchAfter;
    }

    public boolean isMatchAfter() {
        return this.matchAfter;
    }

    @Override // org.springframework.boot.web.servlet.RegistrationBean
    protected String getDescription() {
        Filter filter = getFilter();
        Assert.notNull(filter, "Filter must not be null");
        return "filter " + getOrDeduceName(filter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public FilterRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        Filter filter = getFilter();
        return servletContext.addFilter(getOrDeduceName(filter), filter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public void configure(FilterRegistration.Dynamic registration) {
        super.configure((AbstractFilterRegistrationBean<T>) registration);
        EnumSet<jakarta.servlet.DispatcherType> dispatcherTypes = determineDispatcherTypes();
        Set<String> servletNames = new LinkedHashSet<>();
        for (ServletRegistrationBean<?> servletRegistrationBean : this.servletRegistrationBeans) {
            servletNames.add(servletRegistrationBean.getServletName());
        }
        servletNames.addAll(this.servletNames);
        if (servletNames.isEmpty() && this.urlPatterns.isEmpty()) {
            registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter, DEFAULT_URL_MAPPINGS);
            return;
        }
        if (!servletNames.isEmpty()) {
            registration.addMappingForServletNames(dispatcherTypes, this.matchAfter, StringUtils.toStringArray(servletNames));
        }
        if (!this.urlPatterns.isEmpty()) {
            registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter, StringUtils.toStringArray(this.urlPatterns));
        }
    }

    public String getFilterName() {
        return getOrDeduceName(getFilter());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(getOrDeduceName(this));
        if (this.servletNames.isEmpty() && this.urlPatterns.isEmpty()) {
            builder.append(" urls=").append(Arrays.toString(DEFAULT_URL_MAPPINGS));
        } else {
            if (!this.servletNames.isEmpty()) {
                builder.append(" servlets=").append(this.servletNames);
            }
            if (!this.urlPatterns.isEmpty()) {
                builder.append(" urls=").append(this.urlPatterns);
            }
        }
        builder.append(" order=").append(getOrder());
        return builder.toString();
    }
}
