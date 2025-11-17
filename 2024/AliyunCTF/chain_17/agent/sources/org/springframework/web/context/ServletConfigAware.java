package org.springframework.web.context;

import jakarta.servlet.ServletConfig;
import org.springframework.beans.factory.Aware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/ServletConfigAware.class */
public interface ServletConfigAware extends Aware {
    void setServletConfig(ServletConfig servletConfig);
}
