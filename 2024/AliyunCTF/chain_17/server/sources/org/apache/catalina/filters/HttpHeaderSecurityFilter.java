package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/HttpHeaderSecurityFilter.class */
public class HttpHeaderSecurityFilter extends FilterBase {
    private static final String HSTS_HEADER_NAME = "Strict-Transport-Security";
    private String hstsHeaderValue;
    private static final String ANTI_CLICK_JACKING_HEADER_NAME = "X-Frame-Options";
    private URI antiClickJackingUri;
    private String antiClickJackingHeaderValue;
    private static final String BLOCK_CONTENT_TYPE_SNIFFING_HEADER_NAME = "X-Content-Type-Options";
    private static final String BLOCK_CONTENT_TYPE_SNIFFING_HEADER_VALUE = "nosniff";

    @Deprecated
    private static final String XSS_PROTECTION_HEADER_NAME = "X-XSS-Protection";

    @Deprecated
    private static final String XSS_PROTECTION_HEADER_VALUE = "1; mode=block";
    private final Log log = LogFactory.getLog((Class<?>) HttpHeaderSecurityFilter.class);
    private boolean hstsEnabled = true;
    private int hstsMaxAgeSeconds = 0;
    private boolean hstsIncludeSubDomains = false;
    private boolean hstsPreload = false;
    private boolean antiClickJackingEnabled = true;
    private XFrameOption antiClickJackingOption = XFrameOption.DENY;
    private boolean blockContentTypeSniffingEnabled = true;

    @Deprecated
    private boolean xssProtectionEnabled = false;

    @Override // org.apache.catalina.filters.FilterBase, jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        StringBuilder hstsValue = new StringBuilder("max-age=");
        hstsValue.append(this.hstsMaxAgeSeconds);
        if (this.hstsIncludeSubDomains) {
            hstsValue.append(";includeSubDomains");
        }
        if (this.hstsPreload) {
            hstsValue.append(";preload");
        }
        this.hstsHeaderValue = hstsValue.toString();
        StringBuilder cjValue = new StringBuilder(this.antiClickJackingOption.headerValue);
        if (this.antiClickJackingOption == XFrameOption.ALLOW_FROM) {
            cjValue.append(' ');
            cjValue.append(this.antiClickJackingUri);
        }
        this.antiClickJackingHeaderValue = cjValue.toString();
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (response.isCommitted()) {
                throw new ServletException(sm.getString("httpHeaderSecurityFilter.committed"));
            }
            if (this.hstsEnabled && request.isSecure()) {
                httpResponse.setHeader(HSTS_HEADER_NAME, this.hstsHeaderValue);
            }
            if (this.antiClickJackingEnabled) {
                httpResponse.setHeader(ANTI_CLICK_JACKING_HEADER_NAME, this.antiClickJackingHeaderValue);
            }
            if (this.blockContentTypeSniffingEnabled) {
                httpResponse.setHeader(BLOCK_CONTENT_TYPE_SNIFFING_HEADER_NAME, BLOCK_CONTENT_TYPE_SNIFFING_HEADER_VALUE);
            }
            if (this.xssProtectionEnabled) {
                httpResponse.setHeader(XSS_PROTECTION_HEADER_NAME, XSS_PROTECTION_HEADER_VALUE);
            }
        }
        chain.doFilter(request, response);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected boolean isConfigProblemFatal() {
        return true;
    }

    public boolean isHstsEnabled() {
        return this.hstsEnabled;
    }

    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    public int getHstsMaxAgeSeconds() {
        return this.hstsMaxAgeSeconds;
    }

    public void setHstsMaxAgeSeconds(int hstsMaxAgeSeconds) {
        if (hstsMaxAgeSeconds < 0) {
            this.hstsMaxAgeSeconds = 0;
        } else {
            this.hstsMaxAgeSeconds = hstsMaxAgeSeconds;
        }
    }

    public boolean isHstsIncludeSubDomains() {
        return this.hstsIncludeSubDomains;
    }

    public void setHstsIncludeSubDomains(boolean hstsIncludeSubDomains) {
        this.hstsIncludeSubDomains = hstsIncludeSubDomains;
    }

    public boolean isHstsPreload() {
        return this.hstsPreload;
    }

    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    public boolean isAntiClickJackingEnabled() {
        return this.antiClickJackingEnabled;
    }

    public void setAntiClickJackingEnabled(boolean antiClickJackingEnabled) {
        this.antiClickJackingEnabled = antiClickJackingEnabled;
    }

    public String getAntiClickJackingOption() {
        return this.antiClickJackingOption.toString();
    }

    public void setAntiClickJackingOption(String antiClickJackingOption) {
        for (XFrameOption option : XFrameOption.values()) {
            if (option.getHeaderValue().equalsIgnoreCase(antiClickJackingOption)) {
                this.antiClickJackingOption = option;
                return;
            }
        }
        throw new IllegalArgumentException(sm.getString("httpHeaderSecurityFilter.clickjack.invalid", antiClickJackingOption));
    }

    public String getAntiClickJackingUri() {
        return this.antiClickJackingUri.toString();
    }

    public boolean isBlockContentTypeSniffingEnabled() {
        return this.blockContentTypeSniffingEnabled;
    }

    public void setBlockContentTypeSniffingEnabled(boolean blockContentTypeSniffingEnabled) {
        this.blockContentTypeSniffingEnabled = blockContentTypeSniffingEnabled;
    }

    public void setAntiClickJackingUri(String antiClickJackingUri) {
        try {
            URI uri = new URI(antiClickJackingUri);
            this.antiClickJackingUri = uri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Deprecated
    public boolean isXssProtectionEnabled() {
        return this.xssProtectionEnabled;
    }

    @Deprecated
    public void setXssProtectionEnabled(boolean xssProtectionEnabled) {
        this.xssProtectionEnabled = xssProtectionEnabled;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/HttpHeaderSecurityFilter$XFrameOption.class */
    private enum XFrameOption {
        DENY("DENY"),
        SAME_ORIGIN("SAMEORIGIN"),
        ALLOW_FROM("ALLOW-FROM");

        private final String headerValue;

        XFrameOption(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return this.headerValue;
        }
    }
}
