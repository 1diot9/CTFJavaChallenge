package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.SessionConfig;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/LoadBalancerDrainingValve.class */
public class LoadBalancerDrainingValve extends ValveBase {
    public static final String ATTRIBUTE_KEY_JK_LB_ACTIVATION = "JK_LB_ACTIVATION";
    private int _redirectStatusCode;
    private String _ignoreCookieName;
    private String _ignoreCookieValue;

    public LoadBalancerDrainingValve() {
        super(true);
        this._redirectStatusCode = 307;
    }

    public void setRedirectStatusCode(int code) {
        this._redirectStatusCode = code;
    }

    public String getIgnoreCookieName() {
        return this._ignoreCookieName;
    }

    public void setIgnoreCookieName(String cookieName) {
        this._ignoreCookieName = cookieName;
    }

    public String getIgnoreCookieValue() {
        return this._ignoreCookieValue;
    }

    public void setIgnoreCookieValue(String cookieValue) {
        this._ignoreCookieValue = cookieValue;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if ("DIS".equals(request.getAttribute(ATTRIBUTE_KEY_JK_LB_ACTIVATION)) && !request.isRequestedSessionIdValid()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Load-balancer is in DISABLED state; draining this node");
            }
            boolean ignoreRebalance = false;
            Cookie sessionCookie = null;
            Cookie[] cookies = request.getCookies();
            String sessionCookieName = SessionConfig.getSessionCookieName(request.getContext());
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace("Checking cookie " + cookieName + "=" + cookie.getValue());
                    }
                    if (sessionCookieName.equals(cookieName) && request.getRequestedSessionId().equals(cookie.getValue())) {
                        sessionCookie = cookie;
                    } else if (null != this._ignoreCookieName && this._ignoreCookieName.equals(cookieName) && null != this._ignoreCookieValue && this._ignoreCookieValue.equals(cookie.getValue())) {
                        ignoreRebalance = true;
                    }
                }
            }
            if (ignoreRebalance) {
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug("Client is presenting a valid " + this._ignoreCookieName + " cookie, re-balancing is being skipped");
                }
                getNext().invoke(request, response);
                return;
            }
            if (null != sessionCookie) {
                sessionCookie.setPath(SessionConfig.getSessionCookiePath(request.getContext()));
                sessionCookie.setMaxAge(0);
                sessionCookie.setValue("");
                SessionCookieConfig sessionCookieConfig = request.getContext().getServletContext().getSessionCookieConfig();
                sessionCookie.setSecure(request.isSecure() || sessionCookieConfig.isSecure());
                response.addCookie(sessionCookie);
            }
            String uri = request.getRequestURI();
            String sessionURIParamName = SessionConfig.getSessionUriParamName(request.getContext());
            if (uri.contains(";" + sessionURIParamName + "=")) {
                uri = uri.replaceFirst(";" + sessionURIParamName + "=[^&?]*", "");
            }
            String queryString = request.getQueryString();
            if (null != queryString) {
                uri = uri + "?" + queryString;
            }
            response.setHeader("Location", uri);
            response.setStatus(this._redirectStatusCode);
            return;
        }
        getNext().invoke(request, response);
    }
}
