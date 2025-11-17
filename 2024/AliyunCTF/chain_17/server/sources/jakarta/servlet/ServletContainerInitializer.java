package jakarta.servlet;

import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletContainerInitializer.class */
public interface ServletContainerInitializer {
    void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException;
}
