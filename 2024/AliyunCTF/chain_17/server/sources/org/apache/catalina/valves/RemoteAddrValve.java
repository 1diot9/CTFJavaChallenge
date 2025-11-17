package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/RemoteAddrValve.class */
public final class RemoteAddrValve extends RequestFilterValve {
    private static final Log log = LogFactory.getLog((Class<?>) RemoteAddrValve.class);

    @Override // org.apache.catalina.valves.RequestFilterValve, org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String property;
        if (getUsePeerAddress()) {
            property = request.getPeerAddr();
        } else {
            property = request.getRequest().getRemoteAddr();
        }
        if (getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPortWithOffset();
        }
        process(property, request, response);
    }

    @Override // org.apache.catalina.valves.RequestFilterValve
    protected Log getLog() {
        return log;
    }
}
