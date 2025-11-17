package org.springframework.boot.autoconfigure.web.servlet.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/DefaultErrorViewResolver.class */
public class DefaultErrorViewResolver implements ErrorViewResolver, Ordered {
    private static final Map<HttpStatus.Series, String> SERIES_VIEWS;
    private final ApplicationContext applicationContext;
    private final WebProperties.Resources resources;
    private final TemplateAvailabilityProviders templateAvailabilityProviders;
    private int order = Integer.MAX_VALUE;

    static {
        Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
        views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
        views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
        SERIES_VIEWS = Collections.unmodifiableMap(views);
    }

    public DefaultErrorViewResolver(ApplicationContext applicationContext, WebProperties.Resources resources) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        Assert.notNull(resources, "Resources must not be null");
        this.applicationContext = applicationContext;
        this.resources = resources;
        this.templateAvailabilityProviders = new TemplateAvailabilityProviders(applicationContext);
    }

    DefaultErrorViewResolver(ApplicationContext applicationContext, WebProperties.Resources resourceProperties, TemplateAvailabilityProviders templateAvailabilityProviders) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        Assert.notNull(resourceProperties, "Resources must not be null");
        this.applicationContext = applicationContext;
        this.resources = resourceProperties;
        this.templateAvailabilityProviders = templateAvailabilityProviders;
    }

    @Override // org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
        if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
            modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
        }
        return modelAndView;
    }

    private ModelAndView resolve(String viewName, Map<String, Object> model) {
        String errorViewName = "error/" + viewName;
        TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName, this.applicationContext);
        if (provider != null) {
            return new ModelAndView(errorViewName, (Map<String, ?>) model);
        }
        return resolveResource(errorViewName, model);
    }

    private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
        Resource resource;
        for (String location : this.resources.getStaticLocations()) {
            try {
                resource = this.applicationContext.getResource(location).createRelative(viewName + ".html");
            } catch (Exception e) {
            }
            if (!resource.exists()) {
                continue;
            } else {
                return new ModelAndView(new HtmlResourceView(resource), (Map<String, ?>) model);
            }
        }
        return null;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/DefaultErrorViewResolver$HtmlResourceView.class */
    public static class HtmlResourceView implements View {
        private final Resource resource;

        HtmlResourceView(Resource resource) {
            this.resource = resource;
        }

        @Override // org.springframework.web.servlet.View
        public String getContentType() {
            return "text/html";
        }

        @Override // org.springframework.web.servlet.View
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setContentType(getContentType());
            FileCopyUtils.copy(this.resource.getInputStream(), response.getOutputStream());
        }
    }
}
