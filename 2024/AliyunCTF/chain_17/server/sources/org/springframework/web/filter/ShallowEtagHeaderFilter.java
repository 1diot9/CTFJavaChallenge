package org.springframework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ShallowEtagHeaderFilter.class */
public class ShallowEtagHeaderFilter extends OncePerRequestFilter {
    private static final String DIRECTIVE_NO_STORE = "no-store";
    private static final String STREAMING_ATTRIBUTE = ShallowEtagHeaderFilter.class.getName() + ".STREAMING";
    private boolean writeWeakETag = false;

    public void setWriteWeakETag(boolean writeWeakETag) {
        this.writeWeakETag = writeWeakETag;
    }

    public boolean isWriteWeakETag() {
        return this.writeWeakETag;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse responseToUse = response;
        if (!isAsyncDispatch(request) && !(response instanceof ConditionalContentCachingResponseWrapper)) {
            responseToUse = new ConditionalContentCachingResponseWrapper(response, request);
        }
        filterChain.doFilter(request, responseToUse);
        if (!isAsyncStarted(request) && !isContentCachingDisabled(request)) {
            updateResponse(request, responseToUse);
        }
    }

    private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConditionalContentCachingResponseWrapper wrapper = (ConditionalContentCachingResponseWrapper) WebUtils.getNativeResponse(response, ConditionalContentCachingResponseWrapper.class);
        Assert.notNull(wrapper, "ContentCachingResponseWrapper not found");
        HttpServletResponse rawResponse = (HttpServletResponse) wrapper.getResponse();
        if (isEligibleForEtag(request, wrapper, wrapper.getStatus(), wrapper.getContentInputStream())) {
            String eTag = wrapper.getHeader(HttpHeaders.ETAG);
            if (!StringUtils.hasText(eTag)) {
                eTag = generateETagHeaderValue(wrapper.getContentInputStream(), this.writeWeakETag);
                rawResponse.setHeader(HttpHeaders.ETAG, eTag);
            }
            if (new ServletWebRequest(request, rawResponse).checkNotModified(eTag)) {
                return;
            }
        }
        wrapper.copyBodyToResponse();
    }

    protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response, int responseStatusCode, InputStream inputStream) {
        if (!response.isCommitted() && responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET.matches(request.getMethod())) {
            String cacheControl = response.getHeader(HttpHeaders.CACHE_CONTROL);
            return cacheControl == null || !cacheControl.contains(DIRECTIVE_NO_STORE);
        }
        return false;
    }

    protected String generateETagHeaderValue(InputStream inputStream, boolean isWeak) throws IOException {
        StringBuilder builder = new StringBuilder(37);
        if (isWeak) {
            builder.append("W/");
        }
        builder.append("\"0");
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);
        builder.append('\"');
        return builder.toString();
    }

    public static void disableContentCaching(ServletRequest request) {
        Assert.notNull(request, "ServletRequest must not be null");
        request.setAttribute(STREAMING_ATTRIBUTE, true);
    }

    private static boolean isContentCachingDisabled(HttpServletRequest request) {
        return request.getAttribute(STREAMING_ATTRIBUTE) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ShallowEtagHeaderFilter$ConditionalContentCachingResponseWrapper.class */
    public static class ConditionalContentCachingResponseWrapper extends ContentCachingResponseWrapper {
        private final HttpServletRequest request;

        ConditionalContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override // org.springframework.web.util.ContentCachingResponseWrapper, jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            return (ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || hasETag()) ? getResponse().getOutputStream() : super.getOutputStream();
        }

        @Override // org.springframework.web.util.ContentCachingResponseWrapper, jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public PrintWriter getWriter() throws IOException {
            return (ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || hasETag()) ? getResponse().getWriter() : super.getWriter();
        }

        private boolean hasETag() {
            return StringUtils.hasText(getHeader(HttpHeaders.ETAG));
        }
    }
}
