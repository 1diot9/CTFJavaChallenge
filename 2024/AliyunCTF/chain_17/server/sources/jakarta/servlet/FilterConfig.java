package jakarta.servlet;

import java.util.Enumeration;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/FilterConfig.class */
public interface FilterConfig {
    String getFilterName();

    ServletContext getServletContext();

    String getInitParameter(String str);

    Enumeration<String> getInitParameterNames();
}
