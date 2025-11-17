package jakarta.servlet.http;

import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/PushBuilder.class */
public interface PushBuilder {
    PushBuilder method(String str);

    PushBuilder queryString(String str);

    PushBuilder sessionId(String str);

    PushBuilder setHeader(String str, String str2);

    PushBuilder addHeader(String str, String str2);

    PushBuilder removeHeader(String str);

    PushBuilder path(String str);

    void push();

    String getMethod();

    String getQueryString();

    String getSessionId();

    Set<String> getHeaderNames();

    String getHeader(String str);

    String getPath();
}
