package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/AbstractHtmlInputElementTag.class */
public abstract class AbstractHtmlInputElementTag extends AbstractHtmlElementTag {
    public static final String ONFOCUS_ATTRIBUTE = "onfocus";
    public static final String ONBLUR_ATTRIBUTE = "onblur";
    public static final String ONCHANGE_ATTRIBUTE = "onchange";
    public static final String ACCESSKEY_ATTRIBUTE = "accesskey";
    public static final String DISABLED_ATTRIBUTE = "disabled";
    public static final String READONLY_ATTRIBUTE = "readonly";

    @Nullable
    private String onfocus;

    @Nullable
    private String onblur;

    @Nullable
    private String onchange;

    @Nullable
    private String accesskey;
    private boolean disabled;
    private boolean readonly;

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @Nullable
    protected String getOnfocus() {
        return this.onfocus;
    }

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    @Nullable
    protected String getOnblur() {
        return this.onblur;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @Nullable
    protected String getOnchange() {
        return this.onchange;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    @Nullable
    protected String getAccesskey() {
        return this.accesskey;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDisabled() {
        return this.disabled;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    protected boolean isReadonly() {
        return this.readonly;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementTag
    public void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
        super.writeOptionalAttributes(tagWriter);
        writeOptionalAttribute(tagWriter, ONFOCUS_ATTRIBUTE, getOnfocus());
        writeOptionalAttribute(tagWriter, ONBLUR_ATTRIBUTE, getOnblur());
        writeOptionalAttribute(tagWriter, ONCHANGE_ATTRIBUTE, getOnchange());
        writeOptionalAttribute(tagWriter, ACCESSKEY_ATTRIBUTE, getAccesskey());
        if (isDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        if (isReadonly()) {
            writeOptionalAttribute(tagWriter, READONLY_ATTRIBUTE, READONLY_ATTRIBUTE);
        }
    }
}
