package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/RequestAttributes.class */
public interface RequestAttributes {
    public static final int SCOPE_REQUEST = 0;
    public static final int SCOPE_SESSION = 1;
    public static final String REFERENCE_REQUEST = "request";
    public static final String REFERENCE_SESSION = "session";

    @Nullable
    Object getAttribute(String name, int scope);

    void setAttribute(String name, Object value, int scope);

    void removeAttribute(String name, int scope);

    String[] getAttributeNames(int scope);

    void registerDestructionCallback(String name, Runnable callback, int scope);

    @Nullable
    Object resolveReference(String key);

    String getSessionId();

    Object getSessionMutex();
}
