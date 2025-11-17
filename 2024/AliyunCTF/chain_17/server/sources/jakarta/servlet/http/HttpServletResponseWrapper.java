package jakarta.servlet.http;

import jakarta.servlet.ServletResponseWrapper;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServletResponseWrapper.class */
public class HttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse {
    public HttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    private HttpServletResponse _getHttpServletResponse() {
        return (HttpServletResponse) super.getResponse();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        _getHttpServletResponse().addCookie(cookie);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        return _getHttpServletResponse().containsHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        return _getHttpServletResponse().encodeURL(url);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        return _getHttpServletResponse().encodeRedirectURL(url);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        _getHttpServletResponse().sendError(sc, msg);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        _getHttpServletResponse().sendError(sc);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        _getHttpServletResponse().sendRedirect(location);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        _getHttpServletResponse().setDateHeader(name, date);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long date) {
        _getHttpServletResponse().addDateHeader(name, date);
    }

    public void setHeader(String name, String value) {
        _getHttpServletResponse().setHeader(name, value);
    }

    public void addHeader(String name, String value) {
        _getHttpServletResponse().addHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        _getHttpServletResponse().setIntHeader(name, value);
    }

    public void addIntHeader(String name, int value) {
        _getHttpServletResponse().addIntHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        _getHttpServletResponse().setStatus(sc);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public int getStatus() {
        return _getHttpServletResponse().getStatus();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String getHeader(String name) {
        return _getHttpServletResponse().getHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaders(String name) {
        return _getHttpServletResponse().getHeaders(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaderNames() {
        return _getHttpServletResponse().getHeaderNames();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        _getHttpServletResponse().setTrailerFields(supplier);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Supplier<Map<String, String>> getTrailerFields() {
        return _getHttpServletResponse().getTrailerFields();
    }
}
