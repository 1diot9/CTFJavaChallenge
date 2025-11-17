package org.apache.catalina.util;

import jakarta.servlet.http.HttpServletRequest;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/RequestUtil.class */
public final class RequestUtil {
    public static StringBuffer getRequestURL(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80;
        }
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getRequestURI());
        return url;
    }
}
