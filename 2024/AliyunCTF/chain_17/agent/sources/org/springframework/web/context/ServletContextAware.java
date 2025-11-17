package org.springframework.web.context;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.Aware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/ServletContextAware.class */
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext servletContext);
}
