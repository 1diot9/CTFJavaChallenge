package org.springframework.boot.web.servlet.context;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.factory.Constants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext;
import org.springframework.boot.web.context.MissingWebServerFactoryBeanException;
import org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletContextInitializerBeans;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.metrics.StartupStep;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.ServletContextScope;
import org.springframework.web.context.support.WebApplicationContextUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/ServletWebServerApplicationContext.class */
public class ServletWebServerApplicationContext extends GenericWebApplicationContext implements ConfigurableWebServerApplicationContext {
    private static final Log logger = LogFactory.getLog((Class<?>) ServletWebServerApplicationContext.class);
    public static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";
    private volatile WebServer webServer;
    private ServletConfig servletConfig;
    private String serverNamespace;

    public ServletWebServerApplicationContext() {
    }

    public ServletWebServerApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.GenericWebApplicationContext, org.springframework.context.support.AbstractApplicationContext
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new WebApplicationContextServletContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        registerWebApplicationScopes();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public final void refresh() throws BeansException, IllegalStateException {
        try {
            super.refresh();
        } catch (RuntimeException ex) {
            WebServer webServer = this.webServer;
            if (webServer != null) {
                webServer.stop();
                webServer.destroy();
            }
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.GenericWebApplicationContext, org.springframework.context.support.AbstractApplicationContext
    public void onRefresh() {
        super.onRefresh();
        try {
            createWebServer();
        } catch (Throwable ex) {
            throw new ApplicationContextException("Unable to start web server", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void doClose() {
        if (isActive()) {
            AvailabilityChangeEvent.publish(this, ReadinessState.REFUSING_TRAFFIC);
        }
        super.doClose();
        WebServer webServer = this.webServer;
        if (webServer != null) {
            webServer.destroy();
        }
    }

    private void createWebServer() {
        WebServer webServer = this.webServer;
        ServletContext servletContext = getServletContext();
        if (webServer == null && servletContext == null) {
            StartupStep createWebServer = getApplicationStartup().start("spring.boot.webserver.create");
            ServletWebServerFactory factory = getWebServerFactory();
            createWebServer.tag(Constants.FACTORY, factory.getClass().toString());
            this.webServer = factory.getWebServer(getSelfInitializer());
            createWebServer.end();
            getBeanFactory().registerSingleton("webServerGracefulShutdown", new WebServerGracefulShutdownLifecycle(this.webServer));
            getBeanFactory().registerSingleton("webServerStartStop", new WebServerStartStopLifecycle(this, this.webServer));
        } else if (servletContext != null) {
            try {
                getSelfInitializer().onStartup(servletContext);
            } catch (ServletException ex) {
                throw new ApplicationContextException("Cannot initialize servlet context", ex);
            }
        }
        initPropertySources();
    }

    protected ServletWebServerFactory getWebServerFactory() {
        String[] beanNames = getBeanFactory().getBeanNamesForType(ServletWebServerFactory.class);
        if (beanNames.length == 0) {
            throw new MissingWebServerFactoryBeanException(getClass(), ServletWebServerFactory.class, WebApplicationType.SERVLET);
        }
        if (beanNames.length > 1) {
            throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to multiple ServletWebServerFactory beans : " + StringUtils.arrayToCommaDelimitedString(beanNames));
        }
        return (ServletWebServerFactory) getBeanFactory().getBean(beanNames[0], ServletWebServerFactory.class);
    }

    private ServletContextInitializer getSelfInitializer() {
        return this::selfInitialize;
    }

    private void selfInitialize(ServletContext servletContext) throws ServletException {
        prepareWebApplicationContext(servletContext);
        registerApplicationScope(servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(getBeanFactory(), servletContext);
        for (ServletContextInitializer beans : getServletContextInitializerBeans()) {
            beans.onStartup(servletContext);
        }
    }

    private void registerApplicationScope(ServletContext servletContext) {
        ServletContextScope appScope = new ServletContextScope(servletContext);
        getBeanFactory().registerScope("application", appScope);
        servletContext.setAttribute(ServletContextScope.class.getName(), appScope);
    }

    private void registerWebApplicationScopes() {
        ExistingWebApplicationScopes existingScopes = new ExistingWebApplicationScopes(getBeanFactory());
        WebApplicationContextUtils.registerWebApplicationScopes(getBeanFactory());
        existingScopes.restore();
    }

    protected Collection<ServletContextInitializer> getServletContextInitializerBeans() {
        return new ServletContextInitializerBeans(getBeanFactory(), new Class[0]);
    }

    protected void prepareWebApplicationContext(ServletContext servletContext) {
        Object rootContext = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (rootContext != null) {
            if (rootContext == this) {
                throw new IllegalStateException("Cannot initialize context because there is already a root application context present - check whether you have multiple ServletContextInitializers!");
            }
            return;
        }
        servletContext.log("Initializing Spring embedded WebApplicationContext");
        try {
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this);
            if (logger.isDebugEnabled()) {
                logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
            }
            setServletContext(servletContext);
            if (logger.isInfoEnabled()) {
                long elapsedTime = System.currentTimeMillis() - getStartupDate();
                logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
            }
        } catch (Error | RuntimeException ex) {
            logger.error("Context initialization failed", ex);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
            throw ex;
        }
    }

    @Override // org.springframework.web.context.support.GenericWebApplicationContext, org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        if (getServletContext() == null) {
            return new DefaultResourceLoader.ClassPathContextResource(path, getClassLoader());
        }
        return new ServletContextResource(getServletContext(), path);
    }

    @Override // org.springframework.boot.web.context.WebServerApplicationContext
    public String getServerNamespace() {
        return this.serverNamespace;
    }

    @Override // org.springframework.boot.web.context.ConfigurableWebServerApplicationContext
    public void setServerNamespace(String serverNamespace) {
        this.serverNamespace = serverNamespace;
    }

    @Override // org.springframework.web.context.support.GenericWebApplicationContext, org.springframework.web.context.ConfigurableWebApplicationContext
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override // org.springframework.web.context.support.GenericWebApplicationContext, org.springframework.web.context.ConfigurableWebApplicationContext
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    @Override // org.springframework.boot.web.context.WebServerApplicationContext
    public WebServer getWebServer() {
        return this.webServer;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/ServletWebServerApplicationContext$ExistingWebApplicationScopes.class */
    public static class ExistingWebApplicationScopes {
        private static final Set<String> SCOPES;
        private final ConfigurableListableBeanFactory beanFactory;
        private final Map<String, Scope> scopes = new HashMap();

        static {
            Set<String> scopes = new LinkedHashSet<>();
            scopes.add("request");
            scopes.add("session");
            SCOPES = Collections.unmodifiableSet(scopes);
        }

        public ExistingWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
            for (String scopeName : SCOPES) {
                Scope scope = beanFactory.getRegisteredScope(scopeName);
                if (scope != null) {
                    this.scopes.put(scopeName, scope);
                }
            }
        }

        public void restore() {
            this.scopes.forEach((key, value) -> {
                if (ServletWebServerApplicationContext.logger.isInfoEnabled()) {
                    ServletWebServerApplicationContext.logger.info("Restoring user defined scope " + key);
                }
                this.beanFactory.registerScope(key, value);
            });
        }
    }
}
