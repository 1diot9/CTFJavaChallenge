package org.springframework.web;

import jakarta.servlet.ServletException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/HttpSessionRequiredException.class */
public class HttpSessionRequiredException extends ServletException {

    @Nullable
    private final String expectedAttribute;

    public HttpSessionRequiredException(String msg) {
        super(msg);
        this.expectedAttribute = null;
    }

    public HttpSessionRequiredException(String msg, String expectedAttribute) {
        super(msg);
        this.expectedAttribute = expectedAttribute;
    }

    @Nullable
    public String getExpectedAttribute() {
        return this.expectedAttribute;
    }
}
