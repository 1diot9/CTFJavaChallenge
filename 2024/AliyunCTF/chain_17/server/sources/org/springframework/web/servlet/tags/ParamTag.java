package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/ParamTag.class */
public class ParamTag extends BodyTagSupport {
    private String name = "";

    @Nullable
    private String value;
    private boolean valueSet;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Param param = new Param();
        param.setName(this.name);
        if (this.valueSet) {
            param.setValue(this.value);
        } else if (getBodyContent() != null) {
            param.setValue(getBodyContent().getString().trim());
        }
        ParamAware paramAwareTag = findAncestorWithClass(this, ParamAware.class);
        if (paramAwareTag == null) {
            throw new JspException("The param tag must be a descendant of a tag that supports parameters");
        }
        paramAwareTag.addParam(param);
        return 6;
    }

    public void release() {
        super.release();
        this.name = "";
        this.value = null;
        this.valueSet = false;
    }
}
