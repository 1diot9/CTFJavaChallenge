package org.apache.catalina.connector;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/ResponseFacade.class */
public class ResponseFacade implements HttpServletResponse {
    protected static final StringManager sm = StringManager.getManager((Class<?>) ResponseFacade.class);
    protected Response response;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/ResponseFacade$SetContentTypePrivilegedAction.class */
    private final class SetContentTypePrivilegedAction implements PrivilegedAction<Void> {
        private final String contentType;

        SetContentTypePrivilegedAction(String contentType) {
            this.contentType = contentType;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Void run() {
            ResponseFacade.this.response.setContentType(this.contentType);
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/ResponseFacade$DateHeaderPrivilegedAction.class */
    private final class DateHeaderPrivilegedAction implements PrivilegedAction<Void> {
        private final String name;
        private final long value;
        private final boolean add;

        DateHeaderPrivilegedAction(String name, long value, boolean add) {
            this.name = name;
            this.value = value;
            this.add = add;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Void run() {
            if (this.add) {
                ResponseFacade.this.response.addDateHeader(this.name, this.value);
                return null;
            }
            ResponseFacade.this.response.setDateHeader(this.name, this.value);
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/ResponseFacade$FlushBufferPrivilegedAction.class */
    private static class FlushBufferPrivilegedAction implements PrivilegedExceptionAction<Void> {
        private final Response response;

        FlushBufferPrivilegedAction(Response response) {
            this.response = response;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.response.setAppCommitted(true);
            this.response.flushBuffer();
            return null;
        }
    }

    public ResponseFacade(Response response) {
        this.response = null;
        this.response = response;
    }

    public void clear() {
        this.response = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void finish() {
        checkFacade();
        this.response.setSuspended(true);
    }

    public boolean isFinished() {
        checkFacade();
        return this.response.isSuspended();
    }

    public long getContentWritten() {
        checkFacade();
        return this.response.getContentWritten();
    }

    @Override // jakarta.servlet.ServletResponse
    public String getCharacterEncoding() {
        checkFacade();
        return this.response.getCharacterEncoding();
    }

    @Override // jakarta.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        if (isFinished()) {
            this.response.setSuspended(true);
        }
        return this.response.getOutputStream();
    }

    @Override // jakarta.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        if (isFinished()) {
            this.response.setSuspended(true);
        }
        return this.response.getWriter();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLength(int len) {
        if (isCommitted()) {
            return;
        }
        this.response.setContentLength(len);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLengthLong(long length) {
        if (isCommitted()) {
            return;
        }
        this.response.setContentLengthLong(length);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentType(String type) {
        if (isCommitted()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new SetContentTypePrivilegedAction(type));
        } else {
            this.response.setContentType(type);
        }
    }

    @Override // jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        checkCommitted("coyoteResponse.setBufferSize.ise");
        this.response.setBufferSize(size);
    }

    @Override // jakarta.servlet.ServletResponse
    public int getBufferSize() {
        checkFacade();
        return this.response.getBufferSize();
    }

    @Override // jakarta.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        if (isFinished()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new FlushBufferPrivilegedAction(this.response));
                return;
            } catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw ((IOException) ex);
                }
                return;
            }
        }
        this.response.setAppCommitted(true);
        this.response.flushBuffer();
    }

    @Override // jakarta.servlet.ServletResponse
    public void resetBuffer() {
        checkCommitted("coyoteResponse.resetBuffer.ise");
        this.response.resetBuffer();
    }

    @Override // jakarta.servlet.ServletResponse
    public boolean isCommitted() {
        checkFacade();
        return this.response.isAppCommitted();
    }

    @Override // jakarta.servlet.ServletResponse
    public void reset() {
        checkCommitted("coyoteResponse.reset.ise");
        this.response.reset();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (isCommitted()) {
            return;
        }
        this.response.setLocale(loc);
    }

    @Override // jakarta.servlet.ServletResponse
    public Locale getLocale() {
        checkFacade();
        return this.response.getLocale();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        if (isCommitted()) {
            return;
        }
        this.response.addCookie(cookie);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        checkFacade();
        return this.response.containsHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        checkFacade();
        return this.response.encodeURL(url);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        checkFacade();
        return this.response.encodeRedirectURL(url);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc, msg);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        checkCommitted("coyoteResponse.sendRedirect.ise");
        this.response.setAppCommitted(true);
        this.response.sendRedirect(location);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        if (isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, false));
        } else {
            this.response.setDateHeader(name, date);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long date) {
        if (isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, true));
        } else {
            this.response.addDateHeader(name, date);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }
        this.response.setHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }
        this.response.addHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        if (isCommitted()) {
            return;
        }
        this.response.setIntHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        if (isCommitted()) {
            return;
        }
        this.response.addIntHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        if (isCommitted()) {
            return;
        }
        this.response.setStatus(sc);
    }

    @Override // jakarta.servlet.ServletResponse
    public String getContentType() {
        checkFacade();
        return this.response.getContentType();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setCharacterEncoding(String encoding) {
        checkFacade();
        this.response.setCharacterEncoding(encoding);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public int getStatus() {
        checkFacade();
        return this.response.getStatus();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String getHeader(String name) {
        checkFacade();
        return this.response.getHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaderNames() {
        checkFacade();
        return this.response.getHeaderNames();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaders(String name) {
        checkFacade();
        return this.response.getHeaders(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        checkFacade();
        this.response.setTrailerFields(supplier);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Supplier<Map<String, String>> getTrailerFields() {
        checkFacade();
        return this.response.getTrailerFields();
    }

    private void checkFacade() {
        if (this.response == null) {
            throw new IllegalStateException(sm.getString("responseFacade.nullResponse"));
        }
    }

    private void checkCommitted(String messageKey) {
        if (isCommitted()) {
            throw new IllegalStateException(sm.getString(messageKey));
        }
    }
}
