package org.springframework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter.class */
public class ForwardedHeaderFilter extends OncePerRequestFilter {
    private static final Log logger = LogFactory.getLog((Class<?>) ForwardedHeaderFilter.class);
    private static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
    private boolean removeOnly;
    private boolean relativeRedirects;

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
    }

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public void setRelativeRedirects(boolean relativeRedirects) {
        this.relativeRedirects = relativeRedirects;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (request.getHeader(headerName) != null) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse forwardedHeaderExtractingResponse;
        if (this.removeOnly) {
            filterChain.doFilter(new ForwardedHeaderRemovingRequest(request), response);
            return;
        }
        try {
            HttpServletRequest wrappedRequest = new ForwardedHeaderExtractingRequest(request);
            if (this.relativeRedirects) {
                forwardedHeaderExtractingResponse = RelativeRedirectResponseWrapper.wrapIfNecessary(response, HttpStatus.SEE_OTHER);
            } else {
                forwardedHeaderExtractingResponse = new ForwardedHeaderExtractingResponse(response, wrappedRequest);
            }
            HttpServletResponse wrappedResponse = forwardedHeaderExtractingResponse;
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to apply forwarded headers to " + formatRequest(request), ex);
            }
            response.sendError(400);
        }
    }

    protected String formatRequest(HttpServletRequest request) {
        return "HTTP " + request.getMethod() + " \"" + request.getRequestURI() + "\"";
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        doFilterInternal(request, response, filterChain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderRemovingRequest.class */
    public static class ForwardedHeaderRemovingRequest extends HttpServletRequestWrapper {
        private final Set<String> headerNames;

        public ForwardedHeaderRemovingRequest(HttpServletRequest request) {
            super(request);
            this.headerNames = headerNames(request);
        }

        private static Set<String> headerNames(HttpServletRequest request) {
            Set<String> headerNames = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(Locale.ENGLISH));
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (!ForwardedHeaderFilter.FORWARDED_HEADER_NAMES.contains(name)) {
                    headerNames.add(name);
                }
            }
            return Collections.unmodifiableSet(headerNames);
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        @Nullable
        public String getHeader(String name) {
            if (ForwardedHeaderFilter.FORWARDED_HEADER_NAMES.contains(name)) {
                return null;
            }
            return super.getHeader(name);
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaders(String name) {
            if (ForwardedHeaderFilter.FORWARDED_HEADER_NAMES.contains(name)) {
                return Collections.emptyEnumeration();
            }
            return super.getHeaders(name);
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headerNames);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderExtractingRequest.class */
    public static class ForwardedHeaderExtractingRequest extends ForwardedHeaderRemovingRequest {

        @Nullable
        private final String scheme;
        private final boolean secure;

        @Nullable
        private final String host;
        private final int port;

        @Nullable
        private final InetSocketAddress remoteAddress;
        private final ForwardedPrefixExtractor forwardedPrefixExtractor;

        ForwardedHeaderExtractingRequest(HttpServletRequest servletRequest) {
            super(servletRequest);
            ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
            URI uri = request.getURI();
            HttpHeaders headers = request.getHeaders();
            UriComponents uriComponents = ForwardedHeaderUtils.adaptFromForwardedHeaders(uri, headers).build();
            int port = uriComponents.getPort();
            this.scheme = uriComponents.getScheme();
            this.secure = "https".equals(this.scheme) || "wss".equals(this.scheme);
            this.host = uriComponents.getHost();
            this.port = port == -1 ? this.secure ? 443 : 80 : port;
            this.remoteAddress = ForwardedHeaderUtils.parseForwardedFor(uri, headers, request.getRemoteAddress());
            Supplier<HttpServletRequest> requestSupplier = () -> {
                return (HttpServletRequest) getRequest();
            };
            this.forwardedPrefixExtractor = new ForwardedPrefixExtractor(requestSupplier, this.scheme + "://" + this.host + (port == -1 ? "" : ":" + port));
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        @Nullable
        public String getScheme() {
            return this.scheme;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        @Nullable
        public String getServerName() {
            return this.host;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public int getServerPort() {
            return this.port;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public boolean isSecure() {
            return this.secure;
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public String getContextPath() {
            return this.forwardedPrefixExtractor.getContextPath();
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public String getRequestURI() {
            return this.forwardedPrefixExtractor.getRequestUri();
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public StringBuffer getRequestURL() {
            return this.forwardedPrefixExtractor.getRequestUrl();
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        @Nullable
        public String getRemoteHost() {
            return this.remoteAddress != null ? this.remoteAddress.getHostString() : super.getRemoteHost();
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        @Nullable
        public String getRemoteAddr() {
            return this.remoteAddress != null ? this.remoteAddress.getHostString() : super.getRemoteAddr();
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public int getRemotePort() {
            return this.remoteAddress != null ? this.remoteAddress.getPort() : super.getRemotePort();
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public Object getAttribute(String name) {
            if (name.equals("jakarta.servlet.error.request_uri")) {
                return this.forwardedPrefixExtractor.getErrorRequestUri();
            }
            return super.getAttribute(name);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedPrefixExtractor.class */
    private static class ForwardedPrefixExtractor {
        private final Supplier<HttpServletRequest> delegate;
        private final String baseUrl;
        private String actualRequestUri;

        @Nullable
        private final String forwardedPrefix;

        @Nullable
        private String requestUri = initRequestUri();
        private String requestUrl = initRequestUrl();

        public ForwardedPrefixExtractor(Supplier<HttpServletRequest> delegate, String baseUrl) {
            this.delegate = delegate;
            this.baseUrl = baseUrl;
            this.actualRequestUri = delegate.get().getRequestURI();
            this.forwardedPrefix = initForwardedPrefix(delegate.get());
        }

        @Nullable
        private static String initForwardedPrefix(HttpServletRequest request) {
            String result = null;
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if ("X-Forwarded-Prefix".equalsIgnoreCase(name)) {
                    result = request.getHeader(name);
                }
            }
            if (result != null) {
                StringBuilder prefix = new StringBuilder(result.length());
                String[] rawPrefixes = StringUtils.tokenizeToStringArray(result, ",");
                for (String rawPrefix : rawPrefixes) {
                    int endIndex = rawPrefix.length();
                    while (endIndex > 0 && rawPrefix.charAt(endIndex - 1) == '/') {
                        endIndex--;
                    }
                    prefix.append(endIndex != rawPrefix.length() ? rawPrefix.substring(0, endIndex) : rawPrefix);
                }
                return prefix.toString();
            }
            return null;
        }

        @Nullable
        private String initRequestUri() {
            if (this.forwardedPrefix != null) {
                return this.forwardedPrefix + UrlPathHelper.rawPathInstance.getPathWithinApplication(this.delegate.get());
            }
            return null;
        }

        private String initRequestUrl() {
            return this.baseUrl + (this.requestUri != null ? this.requestUri : this.delegate.get().getRequestURI());
        }

        public String getContextPath() {
            return this.forwardedPrefix != null ? this.forwardedPrefix : this.delegate.get().getContextPath();
        }

        public String getRequestUri() {
            if (this.requestUri == null) {
                return this.delegate.get().getRequestURI();
            }
            recalculatePathsIfNecessary();
            return this.requestUri;
        }

        public StringBuffer getRequestUrl() {
            recalculatePathsIfNecessary();
            return new StringBuffer(this.requestUrl);
        }

        private void recalculatePathsIfNecessary() {
            if (!this.actualRequestUri.equals(this.delegate.get().getRequestURI())) {
                this.actualRequestUri = this.delegate.get().getRequestURI();
                this.requestUri = initRequestUri();
                this.requestUrl = initRequestUrl();
            }
        }

        @Nullable
        public String getErrorRequestUri() {
            HttpServletRequest request = this.delegate.get();
            String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
            if (this.forwardedPrefix == null || requestUri == null) {
                return requestUri;
            }
            ErrorPathRequest errorRequest = new ErrorPathRequest(request);
            return this.forwardedPrefix + UrlPathHelper.rawPathInstance.getPathWithinApplication(errorRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderExtractingResponse.class */
    public static class ForwardedHeaderExtractingResponse extends HttpServletResponseWrapper {
        private static final String FOLDER_SEPARATOR = "/";
        private final HttpServletRequest request;

        ForwardedHeaderExtractingResponse(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void sendRedirect(String location) throws IOException {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(location);
            UriComponents uriComponents = builder.build();
            if (uriComponents.getScheme() != null) {
                super.sendRedirect(location);
                return;
            }
            if (location.startsWith("//")) {
                String scheme = this.request.getScheme();
                super.sendRedirect(builder.scheme(scheme).toUriString());
                return;
            }
            String path = uriComponents.getPath();
            if (path != null) {
                path = path.startsWith("/") ? path : StringUtils.applyRelativePath(this.request.getRequestURI(), path);
            }
            ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(this.request);
            URI uri = httpRequest.getURI();
            HttpHeaders headers = httpRequest.getHeaders();
            String result = ForwardedHeaderUtils.adaptFromForwardedHeaders(uri, headers).replacePath(path).replaceQuery(uriComponents.getQuery()).fragment(uriComponents.getFragment()).build().normalize().toUriString();
            super.sendRedirect(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ForwardedHeaderFilter$ErrorPathRequest.class */
    public static class ErrorPathRequest extends HttpServletRequestWrapper {
        ErrorPathRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }

        @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
        public String getRequestURI() {
            String requestUri = (String) getAttribute("jakarta.servlet.error.request_uri");
            Assert.isTrue(requestUri != null, "Expected ERROR requestUri attribute");
            return requestUri;
        }
    }
}
