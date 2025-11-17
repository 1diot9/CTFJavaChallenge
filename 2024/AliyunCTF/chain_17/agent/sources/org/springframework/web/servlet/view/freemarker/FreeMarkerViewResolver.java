package org.springframework.web.servlet.view.freemarker;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerViewResolver.class */
public class FreeMarkerViewResolver extends AbstractTemplateViewResolver {
    public FreeMarkerViewResolver() {
        setViewClass(requiredViewClass());
    }

    public FreeMarkerViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return FreeMarkerView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == FreeMarkerView.class ? new FreeMarkerView() : super.instantiateView();
    }
}
