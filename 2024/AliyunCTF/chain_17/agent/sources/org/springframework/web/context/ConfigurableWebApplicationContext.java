package org.springframework.web.context;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/ConfigurableWebApplicationContext.class */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {
    public static final String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";
    public static final String SERVLET_CONFIG_BEAN_NAME = "servletConfig";

    void setServletContext(@Nullable ServletContext servletContext);

    void setServletConfig(@Nullable ServletConfig servletConfig);

    @Nullable
    ServletConfig getServletConfig();

    void setNamespace(@Nullable String namespace);

    @Nullable
    String getNamespace();

    void setConfigLocation(String configLocation);

    void setConfigLocations(String... configLocations);

    @Nullable
    String[] getConfigLocations();
}
