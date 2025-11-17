package jakarta.servlet.http;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServletRequestWrapper.class */
public class HttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest {
    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private HttpServletRequest _getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getAuthType() {
        return _getHttpServletRequest().getAuthType();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Cookie[] getCookies() {
        return _getHttpServletRequest().getCookies();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public long getDateHeader(String name) {
        return _getHttpServletRequest().getDateHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getHeader(String name) {
        return _getHttpServletRequest().getHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaders(String name) {
        return _getHttpServletRequest().getHeaders(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaderNames() {
        return _getHttpServletRequest().getHeaderNames();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public int getIntHeader(String name) {
        return _getHttpServletRequest().getIntHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        return _getHttpServletRequest().getHttpServletMapping();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getMethod() {
        return _getHttpServletRequest().getMethod();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getPathInfo() {
        return _getHttpServletRequest().getPathInfo();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        return _getHttpServletRequest().getPathTranslated();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getContextPath() {
        return _getHttpServletRequest().getContextPath();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getQueryString() {
        return _getHttpServletRequest().getQueryString();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getRemoteUser() {
        return _getHttpServletRequest().getRemoteUser();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean isUserInRole(String role) {
        return _getHttpServletRequest().isUserInRole(role);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Principal getUserPrincipal() {
        return _getHttpServletRequest().getUserPrincipal();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getRequestedSessionId() {
        return _getHttpServletRequest().getRequestedSessionId();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getRequestURI() {
        return _getHttpServletRequest().getRequestURI();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        return _getHttpServletRequest().getRequestURL();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String getServletPath() {
        return _getHttpServletRequest().getServletPath();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        return _getHttpServletRequest().getSession(create);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        return _getHttpServletRequest().getSession();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public String changeSessionId() {
        return _getHttpServletRequest().changeSessionId();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        return _getHttpServletRequest().isRequestedSessionIdValid();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromCookie() {
        return _getHttpServletRequest().isRequestedSessionIdFromCookie();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromURL() {
        return _getHttpServletRequest().isRequestedSessionIdFromURL();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return _getHttpServletRequest().authenticate(response);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public void login(String username, String password) throws ServletException {
        _getHttpServletRequest().login(username, password);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public void logout() throws ServletException {
        _getHttpServletRequest().logout();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Collection<Part> getParts() throws IOException, ServletException {
        return _getHttpServletRequest().getParts();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Part getPart(String name) throws IOException, ServletException {
        return _getHttpServletRequest().getPart(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> cls) throws IOException, ServletException {
        return (T) _getHttpServletRequest().upgrade(cls);
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        return _getHttpServletRequest().newPushBuilder();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public Map<String, String> getTrailerFields() {
        return _getHttpServletRequest().getTrailerFields();
    }

    @Override // jakarta.servlet.http.HttpServletRequest
    public boolean isTrailerFieldsReady() {
        return _getHttpServletRequest().isTrailerFieldsReady();
    }
}
