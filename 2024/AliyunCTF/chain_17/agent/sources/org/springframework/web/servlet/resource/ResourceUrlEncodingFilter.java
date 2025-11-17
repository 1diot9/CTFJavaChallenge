package org.springframework.web.servlet.resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter.class */
public class ResourceUrlEncodingFilter extends GenericFilterBean {
    private static final Log logger = LogFactory.getLog((Class<?>) ResourceUrlEncodingFilter.class);

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                ResourceUrlEncodingRequestWrapper wrappedRequest = new ResourceUrlEncodingRequestWrapper(httpRequest);
                ResourceUrlEncodingResponseWrapper wrappedResponse = new ResourceUrlEncodingResponseWrapper(wrappedRequest, httpResponse);
                filterChain.doFilter(wrappedRequest, wrappedResponse);
                return;
            }
        }
        throw new ServletException("ResourceUrlEncodingFilter only supports HTTP requests");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$ResourceUrlEncodingRequestWrapper.class */
    private static class ResourceUrlEncodingRequestWrapper extends HttpServletRequestWrapper {

        @Nullable
        private ResourceUrlProvider resourceUrlProvider;

        @Nullable
        private Integer indexLookupPath;
        private String prefixLookupPath;

        ResourceUrlEncodingRequestWrapper(HttpServletRequest request) {
            super(request);
            this.prefixLookupPath = "";
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public void setAttribute(String name, Object value) {
            super.setAttribute(name, value);
            if (ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR.equals(name) && (value instanceof ResourceUrlProvider)) {
                ResourceUrlProvider urlProvider = (ResourceUrlProvider) value;
                initLookupPath(urlProvider);
            }
        }

        private void initLookupPath(ResourceUrlProvider urlProvider) {
            this.resourceUrlProvider = urlProvider;
            if (this.indexLookupPath == null) {
                UrlPathHelper pathHelper = this.resourceUrlProvider.getUrlPathHelper();
                String requestUri = pathHelper.getRequestUri(this);
                String lookupPath = pathHelper.getLookupPathForRequest(this);
                this.indexLookupPath = Integer.valueOf(requestUri.lastIndexOf(lookupPath));
                if (this.indexLookupPath.intValue() == -1) {
                    throw new LookupPathIndexException(lookupPath, requestUri);
                }
                this.prefixLookupPath = requestUri.substring(0, this.indexLookupPath.intValue());
                if (StringUtils.matchesCharacter(lookupPath, '/') && !StringUtils.matchesCharacter(requestUri, '/')) {
                    String contextPath = pathHelper.getContextPath(this);
                    if (requestUri.equals(contextPath)) {
                        this.indexLookupPath = Integer.valueOf(requestUri.length());
                        this.prefixLookupPath = requestUri;
                    }
                }
            }
        }

        @Nullable
        public String resolveUrlPath(String url) {
            if (this.resourceUrlProvider == null) {
                ResourceUrlEncodingFilter.logger.trace("ResourceUrlProvider not available via request attribute " + ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
                return null;
            }
            if (this.indexLookupPath != null && url.startsWith(this.prefixLookupPath)) {
                int suffixIndex = getEndPathIndex(url);
                String suffix = url.substring(suffixIndex);
                String lookupPath = this.resourceUrlProvider.getForLookupPath(url.substring(this.indexLookupPath.intValue(), suffixIndex));
                if (lookupPath != null) {
                    return this.prefixLookupPath + lookupPath + suffix;
                }
                return null;
            }
            return null;
        }

        private int getEndPathIndex(String path) {
            int end = path.indexOf(63);
            int fragmentIndex = path.indexOf(35);
            if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
                end = fragmentIndex;
            }
            if (end == -1) {
                end = path.length();
            }
            return end;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$ResourceUrlEncodingResponseWrapper.class */
    private static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {
        private final ResourceUrlEncodingRequestWrapper request;

        ResourceUrlEncodingResponseWrapper(ResourceUrlEncodingRequestWrapper request, HttpServletResponse wrapped) {
            super(wrapped);
            this.request = request;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public String encodeURL(String url) {
            String urlPath = this.request.resolveUrlPath(url);
            if (urlPath != null) {
                return super.encodeURL(urlPath);
            }
            return super.encodeURL(url);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceUrlEncodingFilter$LookupPathIndexException.class */
    public static class LookupPathIndexException extends IllegalArgumentException {
        LookupPathIndexException(String lookupPath, String requestUri) {
            super("Failed to find lookupPath '" + lookupPath + "' within requestUri '" + requestUri + "'. This could be because the path has invalid encoded characters or isn't normalized.");
        }
    }
}
