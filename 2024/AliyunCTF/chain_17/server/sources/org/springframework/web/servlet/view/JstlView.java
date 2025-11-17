package org.springframework.web.servlet.view;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/JstlView.class */
public class JstlView extends InternalResourceView {

    @Nullable
    private MessageSource messageSource;

    public JstlView() {
    }

    public JstlView(String url) {
        super(url);
    }

    public JstlView(String url, MessageSource messageSource) {
        this(url);
        this.messageSource = messageSource;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.WebApplicationObjectSupport
    public void initServletContext(ServletContext servletContext) {
        if (this.messageSource != null) {
            this.messageSource = JstlUtils.getJstlAwareMessageSource(servletContext, this.messageSource);
        }
        super.initServletContext(servletContext);
    }

    @Override // org.springframework.web.servlet.view.InternalResourceView
    protected void exposeHelpers(HttpServletRequest request) throws Exception {
        if (this.messageSource != null) {
            JstlUtils.exposeLocalizationContext(request, this.messageSource);
        } else {
            JstlUtils.exposeLocalizationContext(new RequestContext(request, getServletContext()));
        }
    }
}
