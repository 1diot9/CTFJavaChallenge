package org.springframework.boot.web.servlet.view;

import com.samskivert.mustache.Mustache;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/view/MustacheViewResolver.class */
public class MustacheViewResolver extends AbstractTemplateViewResolver {
    private final Mustache.Compiler compiler;
    private String charset;

    public MustacheViewResolver() {
        this.compiler = Mustache.compiler();
        setViewClass(requiredViewClass());
    }

    public MustacheViewResolver(Mustache.Compiler compiler) {
        this.compiler = compiler;
        setViewClass(requiredViewClass());
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        MustacheView view = (MustacheView) super.buildView(viewName);
        view.setCompiler(this.compiler);
        view.setCharset(this.charset);
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == MustacheView.class ? new MustacheView() : super.instantiateView();
    }
}
