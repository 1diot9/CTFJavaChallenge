package org.apache.catalina;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.catalina.connector.Request;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Authenticator.class */
public interface Authenticator {
    boolean authenticate(Request request, HttpServletResponse httpServletResponse) throws IOException;

    void login(String str, String str2, Request request) throws ServletException;

    void logout(Request request);
}
