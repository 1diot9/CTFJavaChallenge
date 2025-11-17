package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ViewResolverRegistry.class */
public class ViewResolverRegistry {

    @Nullable
    private final ContentNegotiationManager contentNegotiationManager;

    @Nullable
    private final ApplicationContext applicationContext;

    @Nullable
    private ContentNegotiatingViewResolver contentNegotiatingResolver;
    private final List<ViewResolver> viewResolvers = new ArrayList(4);

    @Nullable
    private Integer order;

    public ViewResolverRegistry(ContentNegotiationManager contentNegotiationManager, @Nullable ApplicationContext context) {
        this.contentNegotiationManager = contentNegotiationManager;
        this.applicationContext = context;
    }

    public boolean hasRegistrations() {
        return (this.contentNegotiatingResolver == null && this.viewResolvers.isEmpty()) ? false : true;
    }

    public void enableContentNegotiation(View... defaultViews) {
        initContentNegotiatingViewResolver(defaultViews);
    }

    public void enableContentNegotiation(boolean useNotAcceptableStatus, View... defaultViews) {
        ContentNegotiatingViewResolver vr = initContentNegotiatingViewResolver(defaultViews);
        vr.setUseNotAcceptableStatusCode(useNotAcceptableStatus);
    }

    private ContentNegotiatingViewResolver initContentNegotiatingViewResolver(View[] defaultViews) {
        this.order = Integer.valueOf(this.order != null ? this.order.intValue() : Integer.MIN_VALUE);
        if (this.contentNegotiatingResolver != null) {
            if (!ObjectUtils.isEmpty((Object[]) defaultViews) && !CollectionUtils.isEmpty(this.contentNegotiatingResolver.getDefaultViews())) {
                List<View> views = new ArrayList<>(this.contentNegotiatingResolver.getDefaultViews());
                views.addAll(Arrays.asList(defaultViews));
                this.contentNegotiatingResolver.setDefaultViews(views);
            }
        } else {
            this.contentNegotiatingResolver = new ContentNegotiatingViewResolver();
            this.contentNegotiatingResolver.setDefaultViews(Arrays.asList(defaultViews));
            this.contentNegotiatingResolver.setViewResolvers(this.viewResolvers);
            if (this.contentNegotiationManager != null) {
                this.contentNegotiatingResolver.setContentNegotiationManager(this.contentNegotiationManager);
            }
        }
        return this.contentNegotiatingResolver;
    }

    public UrlBasedViewResolverRegistration jsp() {
        return jsp("/WEB-INF/", ".jsp");
    }

    public UrlBasedViewResolverRegistration jsp(String prefix, String suffix) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(prefix);
        resolver.setSuffix(suffix);
        this.viewResolvers.add(resolver);
        return new UrlBasedViewResolverRegistration(resolver);
    }

    public UrlBasedViewResolverRegistration freeMarker() {
        if (!checkBeanOfType(FreeMarkerConfigurer.class)) {
            throw new BeanInitializationException("In addition to a FreeMarker view resolver there must also be a single FreeMarkerConfig bean in this web application context (or its parent): FreeMarkerConfigurer is the usual implementation. This bean may be given any name.");
        }
        FreeMarkerRegistration registration = new FreeMarkerRegistration();
        this.viewResolvers.add(registration.getViewResolver());
        return registration;
    }

    public UrlBasedViewResolverRegistration groovy() {
        if (!checkBeanOfType(GroovyMarkupConfigurer.class)) {
            throw new BeanInitializationException("In addition to a Groovy markup view resolver there must also be a single GroovyMarkupConfig bean in this web application context (or its parent): GroovyMarkupConfigurer is the usual implementation. This bean may be given any name.");
        }
        GroovyMarkupRegistration registration = new GroovyMarkupRegistration();
        this.viewResolvers.add(registration.getViewResolver());
        return registration;
    }

    public UrlBasedViewResolverRegistration scriptTemplate() {
        if (!checkBeanOfType(ScriptTemplateConfigurer.class)) {
            throw new BeanInitializationException("In addition to a script template view resolver there must also be a single ScriptTemplateConfig bean in this web application context (or its parent): ScriptTemplateConfigurer is the usual implementation. This bean may be given any name.");
        }
        ScriptRegistration registration = new ScriptRegistration();
        this.viewResolvers.add(registration.getViewResolver());
        return registration;
    }

    public void beanName() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        this.viewResolvers.add(resolver);
    }

    public void viewResolver(ViewResolver viewResolver) {
        if (viewResolver instanceof ContentNegotiatingViewResolver) {
            throw new BeanInitializationException("addViewResolver cannot be used to configure a ContentNegotiatingViewResolver. Please use the method enableContentNegotiation instead.");
        }
        this.viewResolvers.add(viewResolver);
    }

    public void order(int order) {
        this.order = Integer.valueOf(order);
    }

    private boolean checkBeanOfType(Class<?> beanType) {
        return this.applicationContext == null || !ObjectUtils.isEmpty((Object[]) BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory) this.applicationContext, beanType, false, false));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getOrder() {
        if (this.order != null) {
            return this.order.intValue();
        }
        return Integer.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<ViewResolver> getViewResolvers() {
        if (this.contentNegotiatingResolver != null) {
            return Collections.singletonList(this.contentNegotiatingResolver);
        }
        return this.viewResolvers;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ViewResolverRegistry$FreeMarkerRegistration.class */
    private static class FreeMarkerRegistration extends UrlBasedViewResolverRegistration {
        public FreeMarkerRegistration() {
            super(new FreeMarkerViewResolver());
            getViewResolver().setSuffix(".ftl");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ViewResolverRegistry$GroovyMarkupRegistration.class */
    private static class GroovyMarkupRegistration extends UrlBasedViewResolverRegistration {
        public GroovyMarkupRegistration() {
            super(new GroovyMarkupViewResolver());
            getViewResolver().setSuffix(GroovyTemplateProperties.DEFAULT_SUFFIX);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ViewResolverRegistry$ScriptRegistration.class */
    private static class ScriptRegistration extends UrlBasedViewResolverRegistration {
        public ScriptRegistration() {
            super(new ScriptTemplateViewResolver());
            getViewResolver();
        }
    }
}
