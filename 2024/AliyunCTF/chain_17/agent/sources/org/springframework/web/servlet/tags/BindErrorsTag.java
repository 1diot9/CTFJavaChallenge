package org.springframework.web.servlet.tags;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/BindErrorsTag.class */
public class BindErrorsTag extends HtmlEscapingAwareTag {
    public static final String ERRORS_VARIABLE_NAME = "errors";
    private String name = "";

    @Nullable
    private Errors errors;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws ServletException, JspException {
        this.errors = getRequestContext().getErrors(this.name, isHtmlEscape());
        if (this.errors != null && this.errors.hasErrors()) {
            this.pageContext.setAttribute(ERRORS_VARIABLE_NAME, this.errors, 2);
            return 1;
        }
        return 0;
    }

    public int doEndTag() {
        this.pageContext.removeAttribute(ERRORS_VARIABLE_NAME, 2);
        return 6;
    }

    @Nullable
    public final Errors getErrors() {
        return this.errors;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        this.errors = null;
    }
}
