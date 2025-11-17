package org.springframework.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/GenericFilterBean.class */
public abstract class GenericFilterBean implements Filter, BeanNameAware, EnvironmentAware, EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean {

    @Nullable
    private String beanName;

    @Nullable
    private Environment environment;

    @Nullable
    private ServletContext servletContext;

    @Nullable
    private FilterConfig filterConfig;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Set<String> requiredProperties = new HashSet(4);

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected Environment createEnvironment() {
        return new StandardServletEnvironment();
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws ServletException {
        initFilterBean();
    }

    @Override // jakarta.servlet.Filter
    public void destroy() {
    }

    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }

    @Override // jakarta.servlet.Filter
    public final void init(FilterConfig filterConfig) throws ServletException {
        Assert.notNull(filterConfig, "FilterConfig must not be null");
        this.filterConfig = filterConfig;
        PropertyValues pvs = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
        if (!pvs.isEmpty()) {
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                ResourceLoader resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
                Environment env = this.environment;
                if (env == null) {
                    env = new StandardServletEnvironment();
                }
                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, env));
                initBeanWrapper(bw);
                bw.setPropertyValues(pvs, true);
            } catch (BeansException ex) {
                String msg = "Failed to set bean properties on filter '" + filterConfig.getFilterName() + "': " + ex.getMessage();
                this.logger.error(msg, ex);
                throw new ServletException(msg, ex);
            }
        }
        initFilterBean();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Filter '" + filterConfig.getFilterName() + "' configured for use");
        }
    }

    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    protected void initFilterBean() throws ServletException {
    }

    @Nullable
    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String getFilterName() {
        return this.filterConfig != null ? this.filterConfig.getFilterName() : this.beanName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        if (this.servletContext != null) {
            return this.servletContext;
        }
        throw new IllegalStateException("No ServletContext");
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/GenericFilterBean$FilterConfigPropertyValues.class */
    private static class FilterConfigPropertyValues extends MutablePropertyValues {
        public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties) throws ServletException {
            Set<String> missingProps = !CollectionUtils.isEmpty(requiredProperties) ? new HashSet<>(requiredProperties) : null;
            Enumeration<String> paramNames = config.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String property = paramNames.nextElement();
                Object value = config.getInitParameter(property);
                addPropertyValue(new PropertyValue(property, value));
                if (missingProps != null) {
                    missingProps.remove(property);
                }
            }
            if (!CollectionUtils.isEmpty(missingProps)) {
                throw new ServletException("Initialization from FilterConfig for filter '" + config.getFilterName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}
