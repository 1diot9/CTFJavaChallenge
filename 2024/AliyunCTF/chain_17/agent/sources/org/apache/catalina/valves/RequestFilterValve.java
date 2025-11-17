package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/RequestFilterValve.class */
public abstract class RequestFilterValve extends ValveBase {
    protected volatile Pattern allow;
    protected volatile String allowValue;
    protected volatile boolean allowValid;
    protected volatile Pattern deny;
    protected volatile String denyValue;
    protected volatile boolean denyValid;
    protected int denyStatus;
    private boolean invalidAuthenticationWhenDeny;
    private volatile boolean addConnectorPort;
    private volatile boolean usePeerAddress;

    public abstract void invoke(Request request, Response response) throws IOException, ServletException;

    protected abstract Log getLog();

    public RequestFilterValve() {
        super(true);
        this.allow = null;
        this.allowValue = null;
        this.allowValid = true;
        this.deny = null;
        this.denyValue = null;
        this.denyValid = true;
        this.denyStatus = 403;
        this.invalidAuthenticationWhenDeny = false;
        this.addConnectorPort = false;
        this.usePeerAddress = false;
    }

    public String getAllow() {
        return this.allowValue;
    }

    public void setAllow(String allow) {
        if (allow == null || allow.length() == 0) {
            this.allow = null;
            this.allowValue = null;
            this.allowValid = true;
            return;
        }
        boolean success = false;
        try {
            this.allowValue = allow;
            this.allow = Pattern.compile(allow);
            success = true;
            this.allowValid = true;
        } catch (Throwable th) {
            this.allowValid = success;
            throw th;
        }
    }

    public String getDeny() {
        return this.denyValue;
    }

    public void setDeny(String deny) {
        if (deny == null || deny.length() == 0) {
            this.deny = null;
            this.denyValue = null;
            this.denyValid = true;
            return;
        }
        boolean success = false;
        try {
            this.denyValue = deny;
            this.deny = Pattern.compile(deny);
            success = true;
            this.denyValid = true;
        } catch (Throwable th) {
            this.denyValid = success;
            throw th;
        }
    }

    public final boolean isAllowValid() {
        return this.allowValid;
    }

    public final boolean isDenyValid() {
        return this.denyValid;
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    public boolean getInvalidAuthenticationWhenDeny() {
        return this.invalidAuthenticationWhenDeny;
    }

    public void setInvalidAuthenticationWhenDeny(boolean value) {
        this.invalidAuthenticationWhenDeny = value;
    }

    public boolean getAddConnectorPort() {
        return this.addConnectorPort;
    }

    public void setAddConnectorPort(boolean addConnectorPort) {
        this.addConnectorPort = addConnectorPort;
    }

    public boolean getUsePeerAddress() {
        return this.usePeerAddress;
    }

    public void setUsePeerAddress(boolean usePeerAddress) {
        this.usePeerAddress = usePeerAddress;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (!this.allowValid || !this.denyValid) {
            throw new LifecycleException(sm.getString("requestFilterValve.configInvalid"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        if (!this.allowValid || !this.denyValid) {
            throw new LifecycleException(sm.getString("requestFilterValve.configInvalid"));
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void process(String property, Request request, Response response) throws IOException, ServletException {
        if (isAllowed(property)) {
            getNext().invoke(request, response);
            return;
        }
        if (getLog().isDebugEnabled()) {
            getLog().debug(sm.getString("requestFilterValve.deny", request.getRequestURI(), property));
        }
        denyRequest(request, response);
    }

    protected void denyRequest(Request request, Response response) throws IOException, ServletException {
        Context context;
        if (this.invalidAuthenticationWhenDeny && (context = request.getContext()) != null && context.getPreemptiveAuthentication()) {
            if (request.getCoyoteRequest().getMimeHeaders().getValue("authorization") == null) {
                request.getCoyoteRequest().getMimeHeaders().addValue("authorization").setString("invalid");
            }
            getNext().invoke(request, response);
            return;
        }
        response.sendError(this.denyStatus);
    }

    public boolean isAllowed(String property) {
        Pattern deny = this.deny;
        Pattern allow = this.allow;
        if (deny != null && deny.matcher(property).matches()) {
            return false;
        }
        if (allow != null && allow.matcher(property).matches()) {
            return true;
        }
        if (deny != null && allow == null) {
            return true;
        }
        return false;
    }
}
