package org.springframework.web.servlet.config.annotation;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ResourceHandlerRegistry.class */
public class ResourceHandlerRegistry {
    private final ServletContext servletContext;
    private final ApplicationContext applicationContext;

    @Nullable
    private final ContentNegotiationManager contentNegotiationManager;

    @Nullable
    private final UrlPathHelper pathHelper;
    private final List<ResourceHandlerRegistration> registrations;
    private int order;

    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext) {
        this(applicationContext, servletContext, null);
    }

    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext, @Nullable ContentNegotiationManager contentNegotiationManager) {
        this(applicationContext, servletContext, contentNegotiationManager, null);
    }

    public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext, @Nullable ContentNegotiationManager contentNegotiationManager, @Nullable UrlPathHelper pathHelper) {
        this.registrations = new ArrayList();
        this.order = 2147483646;
        Assert.notNull(applicationContext, "ApplicationContext is required");
        this.applicationContext = applicationContext;
        this.servletContext = servletContext;
        this.contentNegotiationManager = contentNegotiationManager;
        this.pathHelper = pathHelper;
    }

    public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
        ResourceHandlerRegistration registration = new ResourceHandlerRegistration(pathPatterns);
        this.registrations.add(registration);
        return registration;
    }

    public boolean hasMappingForPattern(String pathPattern) {
        for (ResourceHandlerRegistration registration : this.registrations) {
            if (Arrays.asList(registration.getPathPatterns()).contains(pathPattern)) {
                return true;
            }
        }
        return false;
    }

    public ResourceHandlerRegistry setOrder(int order) {
        this.order = order;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public AbstractHandlerMapping getHandlerMapping() {
        if (this.registrations.isEmpty()) {
            return null;
        }
        Map<String, HttpRequestHandler> urlMap = new LinkedHashMap<>();
        for (ResourceHandlerRegistration registration : this.registrations) {
            ResourceHttpRequestHandler handler = getRequestHandler(registration);
            for (String pathPattern : registration.getPathPatterns()) {
                urlMap.put(pathPattern, handler);
            }
        }
        return new SimpleUrlHandlerMapping(urlMap, this.order);
    }

    private ResourceHttpRequestHandler getRequestHandler(ResourceHandlerRegistration registration) {
        ResourceHttpRequestHandler handler = registration.getRequestHandler();
        if (this.pathHelper != null) {
            handler.setUrlPathHelper(this.pathHelper);
        }
        if (this.contentNegotiationManager != null) {
            handler.setContentNegotiationManager(this.contentNegotiationManager);
        }
        handler.setServletContext(this.servletContext);
        handler.setApplicationContext(this.applicationContext);
        try {
            handler.afterPropertiesSet();
            return handler;
        } catch (Throwable ex) {
            throw new BeanInitializationException("Failed to init ResourceHttpRequestHandler", ex);
        }
    }
}
