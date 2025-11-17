package org.springframework.web.server.adapter;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.WebApplicationInitializer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/AbstractReactiveWebInitializer.class */
public abstract class AbstractReactiveWebInitializer implements WebApplicationInitializer {
    public static final String DEFAULT_SERVLET_NAME = "http-handler-adapter";

    protected abstract Class<?>[] getConfigClasses();

    @Override // org.springframework.web.WebApplicationInitializer
    public void onStartup(ServletContext servletContext) throws ServletException {
        String servletName = getServletName();
        Assert.state(StringUtils.hasLength(servletName), "getServletName() must not return null or empty");
        ApplicationContext applicationContext = createApplicationContext();
        Assert.state(applicationContext != null, "createApplicationContext() must not return null");
        refreshApplicationContext(applicationContext);
        registerCloseListener(servletContext, applicationContext);
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);
        ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, servlet);
        if (registration == null) {
            throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. Check if there is another servlet registered under the same name.");
        }
        registration.setLoadOnStartup(1);
        registration.addMapping(getServletMapping());
        registration.setAsyncSupported(true);
    }

    protected String getServletName() {
        return DEFAULT_SERVLET_NAME;
    }

    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Class<?>[] configClasses = getConfigClasses();
        Assert.state(!ObjectUtils.isEmpty((Object[]) configClasses), "No Spring configuration provided through getConfigClasses()");
        context.register(configClasses);
        return context;
    }

    protected void refreshApplicationContext(ApplicationContext context) {
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) context;
            if (!cac.isActive()) {
                cac.refresh();
            }
        }
    }

    protected void registerCloseListener(ServletContext servletContext, ApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) applicationContext;
            servletContext.addListener((ServletContext) new ServletContextDestroyedListener(cac));
        }
    }

    protected String getServletMapping() {
        return "/";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/AbstractReactiveWebInitializer$ServletContextDestroyedListener.class */
    public static class ServletContextDestroyedListener implements ServletContextListener {
        private final ConfigurableApplicationContext applicationContext;

        public ServletContextDestroyedListener(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override // jakarta.servlet.ServletContextListener
        public void contextInitialized(ServletContextEvent sce) {
        }

        @Override // jakarta.servlet.ServletContextListener
        public void contextDestroyed(ServletContextEvent sce) {
            this.applicationContext.close();
        }
    }
}
