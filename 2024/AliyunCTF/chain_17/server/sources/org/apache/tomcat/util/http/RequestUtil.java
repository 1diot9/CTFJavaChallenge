package org.apache.tomcat.util.http;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/RequestUtil.class */
public class RequestUtil {
    private RequestUtil() {
    }

    public static String normalize(String path) {
        return normalize(path, true);
    }

    public static String normalize(String path, boolean replaceBackSlash) {
        if (path == null) {
            return null;
        }
        String normalized = path;
        if (replaceBackSlash && normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        boolean addedTrailingSlash = false;
        if (normalized.endsWith("/.") || normalized.endsWith("/..")) {
            normalized = normalized + "/";
            addedTrailingSlash = true;
        }
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            int index2 = normalized.indexOf("/./");
            if (index2 < 0) {
                break;
            }
            normalized = normalized.substring(0, index2) + normalized.substring(index2 + 2);
        }
        while (true) {
            int index3 = normalized.indexOf("/../");
            if (index3 >= 0) {
                if (index3 == 0) {
                    return null;
                }
                int index22 = normalized.lastIndexOf(47, index3 - 1);
                normalized = normalized.substring(0, index22) + normalized.substring(index3 + 3);
            } else {
                if (normalized.length() > 1 && addedTrailingSlash) {
                    normalized = normalized.substring(0, normalized.length() - 1);
                }
                return normalized;
            }
        }
    }

    public static boolean isSameOrigin(HttpServletRequest request, String origin) {
        StringBuilder target = new StringBuilder();
        String scheme = request.getScheme();
        if (scheme == null) {
            return false;
        }
        String scheme2 = scheme.toLowerCase(Locale.ENGLISH);
        target.append(scheme2);
        target.append("://");
        String host = request.getServerName();
        if (host == null) {
            return false;
        }
        target.append(host);
        int port = request.getServerPort();
        if (target.length() == origin.length()) {
            if ((("http".equals(scheme2) || "ws".equals(scheme2)) && port != 80) || (("https".equals(scheme2) || "wss".equals(scheme2)) && port != 443)) {
                target.append(':');
                target.append(port);
            }
        } else {
            target.append(':');
            target.append(port);
        }
        return origin.equals(target.toString());
    }

    public static boolean isValidOrigin(String origin) {
        if (origin.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            return false;
        }
        if ("null".equals(origin) || origin.startsWith("file://")) {
            return true;
        }
        try {
            URI originURI = new URI(origin);
            return originURI.getScheme() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
