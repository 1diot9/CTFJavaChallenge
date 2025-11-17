package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RequestFilter.class */
public abstract class RequestFilter extends FilterBase {
    protected Pattern allow = null;
    protected Pattern deny = null;
    protected int denyStatus = 403;
    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";

    @Override // jakarta.servlet.Filter
    public abstract void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;

    public String getAllow() {
        if (this.allow == null) {
            return null;
        }
        return this.allow.toString();
    }

    public void setAllow(String allow) {
        if (allow == null || allow.length() == 0) {
            this.allow = null;
        } else {
            this.allow = Pattern.compile(allow);
        }
    }

    public String getDeny() {
        if (this.deny == null) {
            return null;
        }
        return this.deny.toString();
    }

    public void setDeny(String deny) {
        if (deny == null || deny.length() == 0) {
            this.deny = null;
        } else {
            this.deny = Pattern.compile(deny);
        }
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected boolean isConfigProblemFatal() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void process(String property, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAllowed(property)) {
            chain.doFilter(request, response);
        } else {
            if (response instanceof HttpServletResponse) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(sm.getString("requestFilter.deny", ((HttpServletRequest) request).getRequestURI(), property));
                }
                ((HttpServletResponse) response).sendError(this.denyStatus);
                return;
            }
            sendErrorWhenNotHttp(response);
        }
    }

    private boolean isAllowed(String property) {
        if (this.deny != null && this.deny.matcher(property).matches()) {
            return false;
        }
        if (this.allow != null && this.allow.matcher(property).matches()) {
            return true;
        }
        if (this.deny != null && this.allow == null) {
            return true;
        }
        return false;
    }

    private void sendErrorWhenNotHttp(ServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write(sm.getString("http.403"));
        response.getWriter().flush();
    }
}
