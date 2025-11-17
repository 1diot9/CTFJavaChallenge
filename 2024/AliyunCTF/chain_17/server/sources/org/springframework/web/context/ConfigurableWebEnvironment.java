package org.springframework.web.context;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/ConfigurableWebEnvironment.class */
public interface ConfigurableWebEnvironment extends ConfigurableEnvironment {
    void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig);
}
