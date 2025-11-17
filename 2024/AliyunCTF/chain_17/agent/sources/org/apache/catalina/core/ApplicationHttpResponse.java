package org.apache.catalina.core;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationHttpResponse.class */
class ApplicationHttpResponse extends HttpServletResponseWrapper {
    protected boolean included;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationHttpResponse(HttpServletResponse response, boolean included) {
        super(response);
        this.included = false;
        setIncluded(included);
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void reset() {
        if (!this.included || getResponse().isCommitted()) {
            getResponse().reset();
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLength(int len) {
        if (!this.included) {
            getResponse().setContentLength(len);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (!this.included) {
            getResponse().setContentLengthLong(len);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentType(String type) {
        if (!this.included) {
            getResponse().setContentType(type);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (!this.included) {
            getResponse().setLocale(loc);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (!this.included) {
            getResponse().setBufferSize(size);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addCookie(cookie);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addDateHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addIntHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendError(sc);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendError(sc, msg);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendRedirect(location);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setDateHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setIntHeader(name, value);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setStatus(sc);
        }
    }

    void setIncluded(boolean included) {
        this.included = included;
    }

    void setResponse(HttpServletResponse response) {
        super.setResponse((ServletResponse) response);
    }
}
