package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/RemoteHostValve.class */
public final class RemoteHostValve extends RequestFilterValve {
    private static final Log log = LogFactory.getLog((Class<?>) RemoteHostValve.class);

    @Override // org.apache.catalina.valves.RequestFilterValve, org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String property;
        if (getAddConnectorPort()) {
            property = request.getRequest().getRemoteHost() + ";" + request.getConnector().getPortWithOffset();
        } else {
            property = request.getRequest().getRemoteHost();
        }
        process(property, request, response);
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    protected Log getLog() {
        return log;
    }
}
