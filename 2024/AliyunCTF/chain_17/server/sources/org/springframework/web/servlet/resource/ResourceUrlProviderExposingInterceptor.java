package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceUrlProviderExposingInterceptor.class */
public class ResourceUrlProviderExposingInterceptor implements HandlerInterceptor {
    public static final String RESOURCE_URL_PROVIDER_ATTR = ResourceUrlProvider.class.getName();
    private final ResourceUrlProvider resourceUrlProvider;

    public ResourceUrlProviderExposingInterceptor(ResourceUrlProvider resourceUrlProvider) {
        Assert.notNull(resourceUrlProvider, "ResourceUrlProvider is required");
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            request.setAttribute(RESOURCE_URL_PROVIDER_ATTR, this.resourceUrlProvider);
            return true;
        } catch (ResourceUrlEncodingFilter.LookupPathIndexException ex) {
            throw new ServletRequestBindingException(ex.getMessage(), ex);
        }
    }
}
