package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/HtmlEscapeTag.class */
public class HtmlEscapeTag extends RequestContextAwareTag {
    private boolean defaultHtmlEscape;

    public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
        this.defaultHtmlEscape = defaultHtmlEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected int doStartTagInternal() throws JspException {
        getRequestContext().setDefaultHtmlEscape(this.defaultHtmlEscape);
        return 1;
    }
}
