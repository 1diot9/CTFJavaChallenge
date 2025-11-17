package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/TextareaTag.class */
public class TextareaTag extends AbstractHtmlInputElementTag {
    public static final String ROWS_ATTRIBUTE = "rows";
    public static final String COLS_ATTRIBUTE = "cols";
    public static final String ONSELECT_ATTRIBUTE = "onselect";

    @Nullable
    private String rows;

    @Nullable
    private String cols;

    @Nullable
    private String onselect;

    public void setRows(String rows) {
        this.rows = rows;
    }

    @Nullable
    protected String getRows() {
        return this.rows;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    @Nullable
    protected String getCols() {
        return this.cols;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @Nullable
    protected String getOnselect() {
        return this.onselect;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("textarea");
        writeDefaultAttributes(tagWriter);
        writeOptionalAttribute(tagWriter, ROWS_ATTRIBUTE, getRows());
        writeOptionalAttribute(tagWriter, COLS_ATTRIBUTE, getCols());
        writeOptionalAttribute(tagWriter, "onselect", getOnselect());
        String value = getDisplayString(getBoundValue(), getPropertyEditor());
        tagWriter.appendValue("\r\n" + processFieldValue(getName(), value, "textarea"));
        tagWriter.endTag();
        return 0;
    }
}
