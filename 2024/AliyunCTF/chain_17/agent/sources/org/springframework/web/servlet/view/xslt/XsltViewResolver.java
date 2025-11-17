package org.springframework.web.servlet.view.xslt;

import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/xslt/XsltViewResolver.class */
public class XsltViewResolver extends UrlBasedViewResolver {

    @Nullable
    private String sourceKey;

    @Nullable
    private URIResolver uriResolver;

    @Nullable
    private ErrorListener errorListener;

    @Nullable
    private Properties outputProperties;
    private boolean indent = true;
    private boolean cacheTemplates = true;

    public XsltViewResolver() {
        setViewClass(requiredViewClass());
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public void setOutputProperties(Properties outputProperties) {
        this.outputProperties = outputProperties;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return XsltView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView instantiateView() {
        return getViewClass() == XsltView.class ? new XsltView() : super.instantiateView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        XsltView view = (XsltView) super.buildView(viewName);
        if (this.sourceKey != null) {
            view.setSourceKey(this.sourceKey);
        }
        if (this.uriResolver != null) {
            view.setUriResolver(this.uriResolver);
        }
        if (this.errorListener != null) {
            view.setErrorListener(this.errorListener);
        }
        view.setIndent(this.indent);
        if (this.outputProperties != null) {
            view.setOutputProperties(this.outputProperties);
        }
        view.setCacheTemplates(this.cacheTemplates);
        return view;
    }
}
