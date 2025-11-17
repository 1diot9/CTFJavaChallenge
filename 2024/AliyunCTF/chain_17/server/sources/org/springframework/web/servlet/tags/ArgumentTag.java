package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/ArgumentTag.class */
public class ArgumentTag extends BodyTagSupport {

    @Nullable
    private Object value;
    private boolean valueSet;

    public void setValue(Object value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Object argument = null;
        if (this.valueSet) {
            argument = this.value;
        } else if (getBodyContent() != null) {
            argument = getBodyContent().getString().trim();
        }
        ArgumentAware argumentAwareTag = findAncestorWithClass(this, ArgumentAware.class);
        if (argumentAwareTag == null) {
            throw new JspException("The argument tag must be a descendant of a tag that supports arguments");
        }
        argumentAwareTag.addArgument(argument);
        return 6;
    }

    public void release() {
        super.release();
        this.value = null;
        this.valueSet = false;
    }
}
