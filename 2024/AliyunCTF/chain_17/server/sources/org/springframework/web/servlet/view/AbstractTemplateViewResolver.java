package org.springframework.web.servlet.view;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/AbstractTemplateViewResolver.class */
public abstract class AbstractTemplateViewResolver extends UrlBasedViewResolver {
    private boolean exposeRequestAttributes = false;
    private boolean allowRequestOverride = false;
    private boolean exposeSessionAttributes = false;
    private boolean allowSessionOverride = false;
    private boolean exposeSpringMacroHelpers = true;

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return AbstractTemplateView.class;
    }

    public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
        this.exposeRequestAttributes = exposeRequestAttributes;
    }

    public void setAllowRequestOverride(boolean allowRequestOverride) {
        this.allowRequestOverride = allowRequestOverride;
    }

    public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
        this.exposeSessionAttributes = exposeSessionAttributes;
    }

    public void setAllowSessionOverride(boolean allowSessionOverride) {
        this.allowSessionOverride = allowSessionOverride;
    }

    public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
        this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractTemplateView view = (AbstractTemplateView) super.buildView(viewName);
        view.setExposeRequestAttributes(this.exposeRequestAttributes);
        view.setAllowRequestOverride(this.allowRequestOverride);
        view.setExposeSessionAttributes(this.exposeSessionAttributes);
        view.setAllowSessionOverride(this.allowSessionOverride);
        view.setExposeSpringMacroHelpers(this.exposeSpringMacroHelpers);
        return view;
    }
}
