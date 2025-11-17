package org.apache.catalina.util;

import jakarta.servlet.SessionCookieConfig;
import org.apache.catalina.Context;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/SessionConfig.class */
public class SessionConfig {
    private static final String DEFAULT_SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String DEFAULT_SESSION_PARAMETER_NAME = "jsessionid";

    public static String getSessionCookieName(Context context) {
        return getConfiguredSessionCookieName(context, DEFAULT_SESSION_COOKIE_NAME);
    }

    public static String getSessionUriParamName(Context context) {
        return getConfiguredSessionCookieName(context, DEFAULT_SESSION_PARAMETER_NAME);
    }

    private static String getConfiguredSessionCookieName(Context context, String defaultName) {
        if (context != null) {
            String cookieName = context.getSessionCookieName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
            SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
            String cookieName2 = scc.getName();
            if (cookieName2 != null && cookieName2.length() > 0) {
                return cookieName2;
            }
        }
        return defaultName;
    }

    public static String getSessionCookiePath(Context context) {
        SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
        String contextPath = context.getSessionCookiePath();
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = scc.getPath();
        }
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = context.getEncodedPath();
        }
        if (context.getSessionCookiePathUsesTrailingSlash()) {
            if (!contextPath.endsWith("/")) {
                contextPath = contextPath + "/";
            }
        } else if (contextPath.length() == 0) {
            contextPath = "/";
        }
        return contextPath;
    }

    private SessionConfig() {
    }
}
