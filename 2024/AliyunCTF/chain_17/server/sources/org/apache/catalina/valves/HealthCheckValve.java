package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleBase;
import org.apache.tomcat.util.buf.MessageBytes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/HealthCheckValve.class */
public class HealthCheckValve extends ValveBase {
    private static final String UP = "{\n  \"status\": \"UP\",\n  \"checks\": []\n}";
    private static final String DOWN = "{\n  \"status\": \"DOWN\",\n  \"checks\": []\n}";
    private String path;
    protected boolean context;
    protected boolean checkContainersAvailable;

    public HealthCheckValve() {
        super(true);
        this.path = "/health";
        this.context = false;
        this.checkContainersAvailable = true;
    }

    public final String getPath() {
        return this.path;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public boolean getCheckContainersAvailable() {
        return this.checkContainersAvailable;
    }

    public void setCheckContainersAvailable(boolean checkContainersAvailable) {
        this.checkContainersAvailable = checkContainersAvailable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        this.context = getContainer() instanceof Context;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        MessageBytes urlMB = this.context ? request.getRequestPathMB() : request.getDecodedRequestURIMB();
        if (urlMB.equals(this.path)) {
            response.setContentType("application/json");
            if (!this.checkContainersAvailable || isAvailable(getContainer())) {
                response.getOutputStream().print(UP);
                return;
            } else {
                response.setStatus(503);
                response.getOutputStream().print(DOWN);
                return;
            }
        }
        getNext().invoke(request, response);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected boolean isAvailable(Container container) {
        for (Container child : container.findChildren()) {
            if (!isAvailable(child)) {
                return false;
            }
        }
        if (container instanceof LifecycleBase) {
            return ((LifecycleBase) container).getState().isAvailable();
        }
        return true;
    }
}
