package jakarta.servlet.http;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServletRequest.class */
public interface HttpServletRequest extends ServletRequest {
    public static final String BASIC_AUTH = "BASIC";
    public static final String FORM_AUTH = "FORM";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";

    String getAuthType();

    Cookie[] getCookies();

    long getDateHeader(String str);

    String getHeader(String str);

    Enumeration<String> getHeaders(String str);

    Enumeration<String> getHeaderNames();

    int getIntHeader(String str);

    String getMethod();

    String getPathInfo();

    String getPathTranslated();

    String getContextPath();

    String getQueryString();

    String getRemoteUser();

    boolean isUserInRole(String str);

    Principal getUserPrincipal();

    String getRequestedSessionId();

    String getRequestURI();

    StringBuffer getRequestURL();

    String getServletPath();

    HttpSession getSession(boolean z);

    HttpSession getSession();

    String changeSessionId();

    boolean isRequestedSessionIdValid();

    boolean isRequestedSessionIdFromCookie();

    boolean isRequestedSessionIdFromURL();

    boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException;

    void login(String str, String str2) throws ServletException;

    void logout() throws ServletException;

    Collection<Part> getParts() throws IOException, ServletException;

    Part getPart(String str) throws IOException, ServletException;

    <T extends HttpUpgradeHandler> T upgrade(Class<T> cls) throws IOException, ServletException;

    default HttpServletMapping getHttpServletMapping() {
        return new HttpServletMapping() { // from class: jakarta.servlet.http.HttpServletRequest.1
            @Override // jakarta.servlet.http.HttpServletMapping
            public String getMatchValue() {
                return "";
            }

            @Override // jakarta.servlet.http.HttpServletMapping
            public String getPattern() {
                return "";
            }

            @Override // jakarta.servlet.http.HttpServletMapping
            public String getServletName() {
                return "";
            }

            @Override // jakarta.servlet.http.HttpServletMapping
            public MappingMatch getMappingMatch() {
                return null;
            }
        };
    }

    default PushBuilder newPushBuilder() {
        return null;
    }

    default Map<String, String> getTrailerFields() {
        return Collections.emptyMap();
    }

    default boolean isTrailerFieldsReady() {
        return true;
    }
}
