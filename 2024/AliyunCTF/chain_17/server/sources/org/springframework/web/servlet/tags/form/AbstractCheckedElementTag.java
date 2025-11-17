package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/AbstractCheckedElementTag.class */
public abstract class AbstractCheckedElementTag extends AbstractHtmlInputElementTag {
    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getInputType();

    /* JADX INFO: Access modifiers changed from: protected */
    public void renderFromValue(@Nullable Object value, TagWriter tagWriter) throws JspException {
        renderFromValue(value, value, tagWriter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void renderFromValue(@Nullable Object item, @Nullable Object value, TagWriter tagWriter) throws JspException {
        String displayValue = convertToDisplayString(value);
        tagWriter.writeAttribute("value", processFieldValue(getName(), displayValue, getInputType()));
        if (isOptionSelected(value) || (value != item && isOptionSelected(item))) {
            tagWriter.writeAttribute("checked", "checked");
        }
    }

    private boolean isOptionSelected(@Nullable Object value) throws JspException {
        return SelectedValueComparator.isSelected(getBindStatus(), value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void renderFromBoolean(Boolean boundValue, TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("value", processFieldValue(getName(), "true", getInputType()));
        if (boundValue.booleanValue()) {
            tagWriter.writeAttribute("checked", "checked");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    @Nullable
    public String autogenerateId() throws JspException {
        String id = super.autogenerateId();
        if (id != null) {
            return TagIdGenerator.nextId(id, this.pageContext);
        }
        return null;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementTag
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return !"type".equals(localName);
    }
}
