package org.apache.catalina;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Valve.class */
public interface Valve {
    Valve getNext();

    void setNext(Valve valve);

    void backgroundProcess();

    void invoke(Request request, Response response) throws IOException, ServletException;

    boolean isAsyncSupported();
}
