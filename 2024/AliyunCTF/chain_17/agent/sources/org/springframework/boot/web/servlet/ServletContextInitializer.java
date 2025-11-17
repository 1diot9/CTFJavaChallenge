package org.springframework.boot.web.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/ServletContextInitializer.class */
public interface ServletContextInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
