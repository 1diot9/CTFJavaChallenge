package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/AddDefaultCharsetFilter.class */
public class AddDefaultCharsetFilter extends FilterBase {
    private final Log log = LogFactory.getLog((Class<?>) AddDefaultCharsetFilter.class);
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private String encoding;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    @Override // org.apache.catalina.filters.FilterBase, jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (this.encoding == null || this.encoding.length() == 0 || this.encoding.equalsIgnoreCase("default")) {
            this.encoding = "ISO-8859-1";
        } else if (this.encoding.equalsIgnoreCase("system")) {
            this.encoding = Charset.defaultCharset().name();
        } else if (!Charset.isSupported(this.encoding)) {
            throw new IllegalArgumentException(sm.getString("addDefaultCharset.unsupportedCharset", this.encoding));
        }
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            ResponseWrapper wrapped = new ResponseWrapper((HttpServletResponse) response, this.encoding);
            chain.doFilter(request, wrapped);
        } else {
            chain.doFilter(request, response);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/AddDefaultCharsetFilter$ResponseWrapper.class */
    public static class ResponseWrapper extends HttpServletResponseWrapper {
        private String encoding;

        public ResponseWrapper(HttpServletResponse response, String encoding) {
            super(response);
            this.encoding = encoding;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void setContentType(String contentType) {
            if (contentType != null && contentType.startsWith("text/")) {
                if (!contentType.contains("charset=")) {
                    super.setContentType(contentType + ";charset=" + this.encoding);
                    return;
                } else {
                    super.setContentType(contentType);
                    this.encoding = getCharacterEncoding();
                    return;
                }
            }
            super.setContentType(contentType);
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                setContentType(value);
            } else {
                super.setHeader(name, value);
            }
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                setContentType(value);
            } else {
                super.addHeader(name, value);
            }
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void setCharacterEncoding(String charset) {
            super.setCharacterEncoding(charset);
            this.encoding = charset;
        }
    }
}
