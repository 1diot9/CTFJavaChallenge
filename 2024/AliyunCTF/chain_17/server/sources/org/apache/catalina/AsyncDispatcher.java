package org.apache.catalina;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/AsyncDispatcher.class */
public interface AsyncDispatcher {
    void dispatch(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;
}
