package jakarta.servlet;

import java.util.Enumeration;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletConfig.class */
public interface ServletConfig {
    String getServletName();

    ServletContext getServletContext();

    String getInitParameter(String str);

    Enumeration<String> getInitParameterNames();
}
