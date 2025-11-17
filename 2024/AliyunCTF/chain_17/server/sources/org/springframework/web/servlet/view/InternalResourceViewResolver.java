package org.springframework.web.servlet.view;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/InternalResourceViewResolver.class */
public class InternalResourceViewResolver extends UrlBasedViewResolver {
    private static final boolean jstlPresent = ClassUtils.isPresent("jakarta.servlet.jsp.jstl.core.Config", InternalResourceViewResolver.class.getClassLoader());

    @Nullable
    private Boolean alwaysInclude;

    public InternalResourceViewResolver() {
        Class<?> viewClass = requiredViewClass();
        if (InternalResourceView.class == viewClass && jstlPresent) {
            viewClass = JstlView.class;
        }
        setViewClass(viewClass);
    }

    public InternalResourceViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    public void setAlwaysInclude(boolean alwaysInclude) {
        this.alwaysInclude = Boolean.valueOf(alwaysInclude);
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return InternalResourceView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == InternalResourceView.class ? new InternalResourceView() : getViewClass() == JstlView.class ? new JstlView() : super.instantiateView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        InternalResourceView view = (InternalResourceView) super.buildView(viewName);
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude.booleanValue());
        }
        view.setPreventDispatchLoop(true);
        return view;
    }
}
