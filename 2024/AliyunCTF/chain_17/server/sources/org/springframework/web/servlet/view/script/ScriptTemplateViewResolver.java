package org.springframework.web.servlet.view.script;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/script/ScriptTemplateViewResolver.class */
public class ScriptTemplateViewResolver extends UrlBasedViewResolver {
    public ScriptTemplateViewResolver() {
        setViewClass(requiredViewClass());
    }

    public ScriptTemplateViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return ScriptTemplateView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == ScriptTemplateView.class ? new ScriptTemplateView() : super.instantiateView();
    }
}
