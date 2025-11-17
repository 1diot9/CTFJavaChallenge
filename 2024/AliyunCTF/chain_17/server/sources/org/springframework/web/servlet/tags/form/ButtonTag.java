package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/ButtonTag.class */
public class ButtonTag extends AbstractHtmlElementTag {
    public static final String DISABLED_ATTRIBUTE = "disabled";

    @Nullable
    private TagWriter tagWriter;

    @Nullable
    private String name;

    @Nullable
    private String value;
    private boolean disabled;

    public void setName(String name) {
        this.name = name;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    @Nullable
    public String getName() {
        return this.name;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("button");
        writeDefaultAttributes(tagWriter);
        tagWriter.writeAttribute("type", getType());
        writeValue(tagWriter);
        if (isDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        return 1;
    }

    protected void writeValue(TagWriter tagWriter) throws JspException {
        String valueToUse = getValue() != null ? getValue() : getDefaultValue();
        tagWriter.writeAttribute("value", processFieldValue(getName(), valueToUse, getType()));
    }

    protected String getDefaultValue() {
        return "Submit";
    }

    protected String getType() {
        return "submit";
    }

    public int doEndTag() throws JspException {
        Assert.state(this.tagWriter != null, "No TagWriter set");
        this.tagWriter.endTag();
        return 6;
    }
}
