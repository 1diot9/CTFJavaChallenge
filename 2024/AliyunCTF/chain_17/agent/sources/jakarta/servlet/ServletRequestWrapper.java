package jakarta.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletRequestWrapper.class */
public class ServletRequestWrapper implements ServletRequest {
    private static final String LSTRING_FILE = "jakarta.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private ServletRequest request;

    public ServletRequestWrapper(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(lStrings.getString("wrapper.nullRequest"));
        }
        this.request = request;
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(lStrings.getString("wrapper.nullRequest"));
        }
        this.request = request;
    }

    @Override // jakarta.servlet.ServletRequest
    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    }

    @Override // jakarta.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        return this.request.getAttributeNames();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }

    @Override // jakarta.servlet.ServletRequest
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(enc);
    }

    @Override // jakarta.servlet.ServletRequest
    public int getContentLength() {
        return this.request.getContentLength();
    }

    @Override // jakarta.servlet.ServletRequest
    public long getContentLengthLong() {
        return this.request.getContentLengthLong();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getContentType() {
        return this.request.getContentType();
    }

    @Override // jakarta.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }

    @Override // jakarta.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }

    @Override // jakarta.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        return this.request.getParameterNames();
    }

    @Override // jakarta.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        return this.request.getParameterValues(name);
    }

    @Override // jakarta.servlet.ServletRequest
    public String getProtocol() {
        return this.request.getProtocol();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getScheme() {
        return this.request.getScheme();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override // jakarta.servlet.ServletRequest
    public int getServerPort() {
        return this.request.getServerPort();
    }

    @Override // jakarta.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        return this.request.getReader();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }

    @Override // jakarta.servlet.ServletRequest
    public void setAttribute(String name, Object o) {
        this.request.setAttribute(name, o);
    }

    @Override // jakarta.servlet.ServletRequest
    public void removeAttribute(String name) {
        this.request.removeAttribute(name);
    }

    @Override // jakarta.servlet.ServletRequest
    public Locale getLocale() {
        return this.request.getLocale();
    }

    @Override // jakarta.servlet.ServletRequest
    public Enumeration<Locale> getLocales() {
        return this.request.getLocales();
    }

    @Override // jakarta.servlet.ServletRequest
    public boolean isSecure() {
        return this.request.isSecure();
    }

    @Override // jakarta.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        return this.request.getRequestDispatcher(path);
    }

    @Override // jakarta.servlet.ServletRequest
    public int getRemotePort() {
        return this.request.getRemotePort();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getLocalName() {
        return this.request.getLocalName();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getLocalAddr() {
        return this.request.getLocalAddr();
    }

    @Override // jakarta.servlet.ServletRequest
    public int getLocalPort() {
        return this.request.getLocalPort();
    }

    @Override // jakarta.servlet.ServletRequest
    public ServletContext getServletContext() {
        return this.request.getServletContext();
    }

    @Override // jakarta.servlet.ServletRequest
    public AsyncContext startAsync() throws IllegalStateException {
        return this.request.startAsync();
    }

    @Override // jakarta.servlet.ServletRequest
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return this.request.startAsync(servletRequest, servletResponse);
    }

    @Override // jakarta.servlet.ServletRequest
    public boolean isAsyncStarted() {
        return this.request.isAsyncStarted();
    }

    @Override // jakarta.servlet.ServletRequest
    public boolean isAsyncSupported() {
        return this.request.isAsyncSupported();
    }

    @Override // jakarta.servlet.ServletRequest
    public AsyncContext getAsyncContext() {
        return this.request.getAsyncContext();
    }

    public boolean isWrapperFor(ServletRequest wrapped) {
        if (this.request == wrapped) {
            return true;
        }
        if (this.request instanceof ServletRequestWrapper) {
            return ((ServletRequestWrapper) this.request).isWrapperFor(wrapped);
        }
        return false;
    }

    public boolean isWrapperFor(Class<?> wrappedType) {
        if (wrappedType.isAssignableFrom(this.request.getClass())) {
            return true;
        }
        if (this.request instanceof ServletRequestWrapper) {
            return ((ServletRequestWrapper) this.request).isWrapperFor(wrappedType);
        }
        return false;
    }

    @Override // jakarta.servlet.ServletRequest
    public DispatcherType getDispatcherType() {
        return this.request.getDispatcherType();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getRequestId() {
        return this.request.getRequestId();
    }

    @Override // jakarta.servlet.ServletRequest
    public String getProtocolRequestId() {
        return this.request.getProtocolRequestId();
    }

    @Override // jakarta.servlet.ServletRequest
    public ServletConnection getServletConnection() {
        return this.request.getServletConnection();
    }
}
