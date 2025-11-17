package org.springframework.web.context.support;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletContextAware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/GenericWebApplicationContext.class */
public class GenericWebApplicationContext extends GenericApplicationContext implements ConfigurableWebApplicationContext, ThemeSource {

    @Nullable
    private ServletContext servletContext;

    @Nullable
    private ThemeSource themeSource;

    public GenericWebApplicationContext() {
    }

    public GenericWebApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory, ServletContext servletContext) {
        super(beanFactory);
        this.servletContext = servletContext;
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.web.context.WebApplicationContext
    @Nullable
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ApplicationContext
    public String getApplicationName() {
        return this.servletContext != null ? this.servletContext.getContextPath() : "";
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (this.servletContext != null) {
            beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext));
            beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        }
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext);
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        Assert.state(this.servletContext != null, "No ServletContext available");
        return new ServletContextResource(this.servletContext, path);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ServletContextResourcePatternResolver(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void onRefresh() {
        this.themeSource = UiApplicationContextUtils.initThemeSource(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void initPropertySources() {
        ConfigurableEnvironment env = getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ConfigurableWebEnvironment configurableWebEnv = (ConfigurableWebEnvironment) env;
            configurableWebEnv.initPropertySources(this.servletContext, null);
        }
    }

    @Override // org.springframework.ui.context.ThemeSource
    @Nullable
    @Deprecated
    public Theme getTheme(String themeName) {
        Assert.state(this.themeSource != null, "No ThemeSource available");
        return this.themeSource.getTheme(themeName);
    }

    public void setServletConfig(@Nullable ServletConfig servletConfig) {
    }

    @Nullable
    public ServletConfig getServletConfig() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getServletConfig()");
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setNamespace(@Nullable String namespace) {
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    @Nullable
    public String getNamespace() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getNamespace()");
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setConfigLocation(String configLocation) {
        if (StringUtils.hasText(configLocation)) {
            throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocation(). Do you still have a 'contextConfigLocation' init-param set?");
        }
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setConfigLocations(String... configLocations) {
        if (!ObjectUtils.isEmpty((Object[]) configLocations)) {
            throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocations(). Do you still have a 'contextConfigLocations' init-param set?");
        }
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public String[] getConfigLocations() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getConfigLocations()");
    }
}
