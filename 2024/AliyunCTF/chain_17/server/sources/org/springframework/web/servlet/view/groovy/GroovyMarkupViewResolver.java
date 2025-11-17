package org.springframework.web.servlet.view.groovy;

import java.util.Locale;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/groovy/GroovyMarkupViewResolver.class */
public class GroovyMarkupViewResolver extends AbstractTemplateViewResolver {
    public GroovyMarkupViewResolver() {
        setViewClass(requiredViewClass());
    }

    public GroovyMarkupViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return GroovyMarkupView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == GroovyMarkupView.class ? new GroovyMarkupView() : super.instantiateView();
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver, org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + "_" + locale;
    }
}
