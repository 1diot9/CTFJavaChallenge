package org.springframework.web.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
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
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/HttpServletBean.class */
public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {

    @Nullable
    private ConfigurableEnvironment environment;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Set<String> requiredProperties = new HashSet(4);

    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf((Class<?>) ConfigurableEnvironment.class, environment, "ConfigurableEnvironment required");
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    @Override // jakarta.servlet.GenericServlet
    public final void init() throws ServletException {
        PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
        if (!pvs.isEmpty()) {
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
                initBeanWrapper(bw);
                bw.setPropertyValues(pvs, true);
            } catch (BeansException ex) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
                }
                throw ex;
            }
        }
        initServletBean();
    }

    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    protected void initServletBean() throws ServletException {
    }

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.ServletConfig
    @Nullable
    public String getServletName() {
        if (getServletConfig() != null) {
            return getServletConfig().getServletName();
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/HttpServletBean$ServletConfigPropertyValues.class */
    private static class ServletConfigPropertyValues extends MutablePropertyValues {
        public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties) throws ServletException {
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
                throw new ServletException("Initialization from ServletConfig for servlet '" + config.getServletName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}
