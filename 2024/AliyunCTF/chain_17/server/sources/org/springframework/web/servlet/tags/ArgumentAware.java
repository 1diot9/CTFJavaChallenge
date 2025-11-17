package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspTagException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/ArgumentAware.class */
public interface ArgumentAware {
    void addArgument(@Nullable Object argument) throws JspTagException;
}
