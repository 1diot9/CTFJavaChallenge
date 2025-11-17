package org.springframework.web.multipart.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/MultipartFilter.class */
public class MultipartFilter extends OncePerRequestFilter {
    public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";
    private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();
    private String multipartResolverBeanName = DEFAULT_MULTIPART_RESOLVER_BEAN_NAME;

    public void setMultipartResolverBeanName(String multipartResolverBeanName) {
        this.multipartResolverBeanName = multipartResolverBeanName;
    }

    protected String getMultipartResolverBeanName() {
        return this.multipartResolverBeanName;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MultipartResolver multipartResolver = lookupMultipartResolver(request);
        HttpServletRequest processedRequest = request;
        if (multipartResolver.isMultipart(processedRequest)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Resolving multipart request");
            }
            processedRequest = multipartResolver.resolveMultipart(processedRequest);
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace("Not a multipart request");
        }
        try {
            filterChain.doFilter(processedRequest, response);
            if (processedRequest instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) processedRequest;
                multipartResolver.cleanupMultipart(multipartRequest);
            }
        } catch (Throwable th) {
            if (processedRequest instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest2 = (MultipartHttpServletRequest) processedRequest;
                multipartResolver.cleanupMultipart(multipartRequest2);
            }
            throw th;
        }
    }

    protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
        return lookupMultipartResolver();
    }

    protected MultipartResolver lookupMultipartResolver() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        String beanName = getMultipartResolverBeanName();
        if (wac != null && wac.containsBean(beanName)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
            }
            return (MultipartResolver) wac.getBean(beanName, MultipartResolver.class);
        }
        return this.defaultMultipartResolver;
    }
}
