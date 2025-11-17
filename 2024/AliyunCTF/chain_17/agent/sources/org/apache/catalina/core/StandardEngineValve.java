package org.apache.catalina.core;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardEngineValve.class */
final class StandardEngineValve extends ValveBase {
    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardEngineValve() {
        super(true);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Host host = request.getHost();
        if (host == null) {
            if (!response.isError()) {
                response.sendError(404);
            }
        } else {
            if (request.isAsyncSupported()) {
                request.setAsyncSupported(host.getPipeline().isAsyncSupported());
            }
            host.getPipeline().getFirst().invoke(request, response);
        }
    }
}
