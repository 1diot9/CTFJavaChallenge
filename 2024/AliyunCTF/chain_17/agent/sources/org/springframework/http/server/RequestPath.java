package org.springframework.http.server;

import java.net.URI;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/RequestPath.class */
public interface RequestPath extends PathContainer {
    PathContainer contextPath();

    PathContainer pathWithinApplication();

    RequestPath modifyContextPath(String contextPath);

    static RequestPath parse(URI uri, @Nullable String contextPath) {
        return parse(uri.getRawPath(), contextPath);
    }

    static RequestPath parse(String rawPath, @Nullable String contextPath) {
        return new DefaultRequestPath(rawPath, contextPath);
    }
}
