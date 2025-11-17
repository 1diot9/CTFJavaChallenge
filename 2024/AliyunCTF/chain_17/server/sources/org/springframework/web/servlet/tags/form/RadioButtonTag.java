package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/RadioButtonTag.class */
public class RadioButtonTag extends AbstractSingleCheckedElementTag {
    @Override // org.springframework.web.servlet.tags.form.AbstractSingleCheckedElementTag
    protected void writeTagDetails(TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("type", getInputType());
        Object resolvedValue = evaluate("value", getValue());
        renderFromValue(resolvedValue, tagWriter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag
    public String getInputType() {
        return "radio";
    }
}
