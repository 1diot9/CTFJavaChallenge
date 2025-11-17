package jakarta.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletResponseWrapper.class */
public class ServletResponseWrapper implements ServletResponse {
    private static final String LSTRING_FILE = "jakarta.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private ServletResponse response;

    public ServletResponseWrapper(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException(lStrings.getString("wrapper.nullResponse"));
        }
        this.response = response;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException(lStrings.getString("wrapper.nullResponse"));
        }
        this.response = response;
    }

    @Override // jakarta.servlet.ServletResponse
    public void setCharacterEncoding(String charset) {
        this.response.setCharacterEncoding(charset);
    }

    @Override // jakarta.servlet.ServletResponse
    public String getCharacterEncoding() {
        return this.response.getCharacterEncoding();
    }

    @Override // jakarta.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.response.getOutputStream();
    }

    @Override // jakarta.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLength(int len) {
        this.response.setContentLength(len);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLengthLong(long length) {
        this.response.setContentLengthLong(length);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentType(String type) {
        this.response.setContentType(type);
    }

    @Override // jakarta.servlet.ServletResponse
    public String getContentType() {
        return this.response.getContentType();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        this.response.setBufferSize(size);
    }

    @Override // jakarta.servlet.ServletResponse
    public int getBufferSize() {
        return this.response.getBufferSize();
    }

    @Override // jakarta.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        this.response.flushBuffer();
    }

    @Override // jakarta.servlet.ServletResponse
    public boolean isCommitted() {
        return this.response.isCommitted();
    }

    @Override // jakarta.servlet.ServletResponse
    public void reset() {
        this.response.reset();
    }

    @Override // jakarta.servlet.ServletResponse
    public void resetBuffer() {
        this.response.resetBuffer();
    }

    @Override // jakarta.servlet.ServletResponse
    public void setLocale(Locale loc) {
        this.response.setLocale(loc);
    }

    @Override // jakarta.servlet.ServletResponse
    public Locale getLocale() {
        return this.response.getLocale();
    }

    public boolean isWrapperFor(ServletResponse wrapped) {
        if (this.response == wrapped) {
            return true;
        }
        if (this.response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) this.response).isWrapperFor(wrapped);
        }
        return false;
    }

    public boolean isWrapperFor(Class<?> wrappedType) {
        if (wrappedType.isAssignableFrom(this.response.getClass())) {
            return true;
        }
        if (this.response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) this.response).isWrapperFor(wrappedType);
        }
        return false;
    }
}
