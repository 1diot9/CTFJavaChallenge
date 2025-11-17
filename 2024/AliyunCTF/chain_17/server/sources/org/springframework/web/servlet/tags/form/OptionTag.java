package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.util.TagUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/OptionTag.class */
public class OptionTag extends AbstractHtmlElementBodyTag implements BodyTag {
    public static final String VALUE_VARIABLE_NAME = "value";
    public static final String DISPLAY_VALUE_VARIABLE_NAME = "displayValue";
    private static final String SELECTED_ATTRIBUTE = "selected";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String DISABLED_ATTRIBUTE = "disabled";

    @Nullable
    private Object value;

    @Nullable
    private String label;

    @Nullable
    private Object oldValue;

    @Nullable
    private Object oldDisplayValue;
    private boolean disabled;

    public void setValue(Object value) {
        this.value = value;
    }

    @Nullable
    protected Object getValue() {
        return this.value;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean isDisabled() {
        return this.disabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Nullable
    protected String getLabel() {
        return this.label;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
        Object value = this.pageContext.getAttribute("value");
        String label = getLabelValue(value);
        renderOption(value, label, tagWriter);
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void renderFromBodyContent(BodyContent bodyContent, TagWriter tagWriter) throws JspException {
        Object value = this.pageContext.getAttribute("value");
        String label = bodyContent.getString();
        renderOption(value, label, tagWriter);
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void onWriteTagContent() {
        assertUnderSelectTag();
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void exposeAttributes() throws JspException {
        Object value = resolveValue();
        this.oldValue = this.pageContext.getAttribute("value");
        this.pageContext.setAttribute("value", value);
        this.oldDisplayValue = this.pageContext.getAttribute(DISPLAY_VALUE_VARIABLE_NAME);
        this.pageContext.setAttribute(DISPLAY_VALUE_VARIABLE_NAME, getDisplayString(value, getBindStatus().getEditor()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    public BindStatus getBindStatus() {
        return (BindStatus) this.pageContext.getAttribute(SelectTag.LIST_VALUE_PAGE_ATTRIBUTE);
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void removeAttributes() {
        if (this.oldValue != null) {
            this.pageContext.setAttribute("value", this.oldValue);
            this.oldValue = null;
        } else {
            this.pageContext.removeAttribute("value");
        }
        if (this.oldDisplayValue != null) {
            this.pageContext.setAttribute(DISPLAY_VALUE_VARIABLE_NAME, this.oldDisplayValue);
            this.oldDisplayValue = null;
        } else {
            this.pageContext.removeAttribute(DISPLAY_VALUE_VARIABLE_NAME);
        }
    }

    private void renderOption(Object value, String label, TagWriter tagWriter) throws JspException {
        tagWriter.startTag("option");
        writeOptionalAttribute(tagWriter, "id", resolveId());
        writeOptionalAttributes(tagWriter);
        String renderedValue = getDisplayString(value, getBindStatus().getEditor());
        tagWriter.writeAttribute("value", processFieldValue(getSelectTag().getName(), renderedValue, "option"));
        if (isSelected(value)) {
            tagWriter.writeAttribute(SELECTED_ATTRIBUTE, SELECTED_ATTRIBUTE);
        }
        if (isDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        tagWriter.appendValue(label);
        tagWriter.endTag();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    public String autogenerateId() throws JspException {
        return null;
    }

    private String getLabelValue(Object resolvedValue) throws JspException {
        String label = getLabel();
        Object labelObj = label == null ? resolvedValue : evaluate("label", label);
        return getDisplayString(labelObj, getBindStatus().getEditor());
    }

    private void assertUnderSelectTag() {
        TagUtils.assertHasAncestorOfType(this, SelectTag.class, "option", "select");
    }

    private SelectTag getSelectTag() {
        return findAncestorWithClass(this, SelectTag.class);
    }

    private boolean isSelected(Object resolvedValue) {
        return SelectedValueComparator.isSelected(getBindStatus(), resolvedValue);
    }

    @Nullable
    private Object resolveValue() throws JspException {
        return evaluate("value", getValue());
    }
}
