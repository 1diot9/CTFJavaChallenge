package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.beans.PropertyEditor;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.web.util.TagUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/TransformTag.class */
public class TransformTag extends HtmlEscapingAwareTag {

    @Nullable
    private Object value;

    @Nullable
    private String var;
    private String scope = TagUtils.SCOPE_PAGE;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws JspException {
        String result;
        if (this.value != null) {
            EditorAwareTag tag = TagSupport.findAncestorWithClass(this, EditorAwareTag.class);
            if (tag == null) {
                throw new JspException("TransformTag can only be used within EditorAwareTag (e.g. BindTag)");
            }
            PropertyEditor editor = tag.getEditor();
            if (editor != null) {
                editor.setValue(this.value);
                result = editor.getAsText();
            } else {
                result = this.value.toString();
            }
            String result2 = htmlEscape(result);
            if (this.var != null) {
                this.pageContext.setAttribute(this.var, result2, TagUtils.getScope(this.scope));
                return 0;
            }
            try {
                this.pageContext.getOut().print(result2);
                return 0;
            } catch (IOException ex) {
                throw new JspException(ex);
            }
        }
        return 0;
    }
}
