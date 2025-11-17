package org.springframework.web.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/WebAppRootListener.class */
public class WebAppRootListener implements ServletContextListener {
    @Override // jakarta.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
        WebUtils.setWebAppRootSystemProperty(event.getServletContext());
    }

    @Override // jakarta.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        WebUtils.removeWebAppRootSystemProperty(event.getServletContext());
    }
}
