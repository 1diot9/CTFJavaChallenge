package org.springframework.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/WebApplicationInitializer.class */
public interface WebApplicationInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
