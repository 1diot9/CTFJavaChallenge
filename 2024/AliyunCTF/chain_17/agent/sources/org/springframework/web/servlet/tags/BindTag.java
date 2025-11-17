package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspTagException;
import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.BindStatus;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/BindTag.class */
public class BindTag extends HtmlEscapingAwareTag implements EditorAwareTag {
    public static final String STATUS_VARIABLE_NAME = "status";
    private String path = "";
    private boolean ignoreNestedPath = false;

    @Nullable
    private BindStatus status;

    @Nullable
    private Object previousPageStatus;

    @Nullable
    private Object previousRequestStatus;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setIgnoreNestedPath(boolean ignoreNestedPath) {
        this.ignoreNestedPath = ignoreNestedPath;
    }

    public boolean isIgnoreNestedPath() {
        return this.ignoreNestedPath;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws Exception {
        String nestedPath;
        String resolvedPath = getPath();
        if (!isIgnoreNestedPath() && (nestedPath = (String) this.pageContext.getAttribute(NestedPathTag.NESTED_PATH_VARIABLE_NAME, 2)) != null && !resolvedPath.startsWith(nestedPath) && !resolvedPath.equals(nestedPath.substring(0, nestedPath.length() - 1))) {
            resolvedPath = nestedPath + resolvedPath;
        }
        try {
            this.status = new BindStatus(getRequestContext(), resolvedPath, isHtmlEscape());
            this.previousPageStatus = this.pageContext.getAttribute(STATUS_VARIABLE_NAME, 1);
            this.previousRequestStatus = this.pageContext.getAttribute(STATUS_VARIABLE_NAME, 2);
            this.pageContext.removeAttribute(STATUS_VARIABLE_NAME, 1);
            this.pageContext.setAttribute(STATUS_VARIABLE_NAME, this.status, 2);
            return 1;
        } catch (IllegalStateException ex) {
            throw new JspTagException(ex.getMessage());
        }
    }

    public int doEndTag() {
        if (this.previousPageStatus != null) {
            this.pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousPageStatus, 1);
        }
        if (this.previousRequestStatus != null) {
            this.pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousRequestStatus, 2);
            return 6;
        }
        this.pageContext.removeAttribute(STATUS_VARIABLE_NAME, 2);
        return 6;
    }

    private BindStatus getStatus() {
        Assert.state(this.status != null, "No current BindStatus");
        return this.status;
    }

    @Nullable
    public final String getProperty() {
        return getStatus().getExpression();
    }

    @Nullable
    public final Errors getErrors() {
        return getStatus().getErrors();
    }

    @Override // org.springframework.web.servlet.tags.EditorAwareTag
    @Nullable
    public final PropertyEditor getEditor() {
        return getStatus().getEditor();
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        this.status = null;
        this.previousPageStatus = null;
        this.previousRequestStatus = null;
    }
}
