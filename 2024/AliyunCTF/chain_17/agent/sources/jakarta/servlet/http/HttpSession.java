package jakarta.servlet.http;

import jakarta.servlet.ServletContext;
import java.util.Enumeration;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSession.class */
public interface HttpSession {
    long getCreationTime();

    String getId();

    long getLastAccessedTime();

    ServletContext getServletContext();

    void setMaxInactiveInterval(int i);

    int getMaxInactiveInterval();

    Object getAttribute(String str);

    Enumeration<String> getAttributeNames();

    void setAttribute(String str, Object obj);

    void removeAttribute(String str);

    void invalidate();

    boolean isNew();
}
