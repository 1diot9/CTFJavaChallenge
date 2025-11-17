package jakarta.servlet;

import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/SessionCookieConfig.class */
public interface SessionCookieConfig {
    void setName(String str);

    String getName();

    void setDomain(String str);

    String getDomain();

    void setPath(String str);

    String getPath();

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    void setComment(String str);

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    String getComment();

    void setHttpOnly(boolean z);

    boolean isHttpOnly();

    void setSecure(boolean z);

    boolean isSecure();

    void setMaxAge(int i);

    int getMaxAge();

    void setAttribute(String str, String str2);

    String getAttribute(String str);

    Map<String, String> getAttributes();
}
