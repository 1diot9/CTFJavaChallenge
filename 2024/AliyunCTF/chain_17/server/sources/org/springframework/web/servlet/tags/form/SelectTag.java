package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.support.BindStatus;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/SelectTag.class */
public class SelectTag extends AbstractHtmlInputElementTag {
    public static final String LIST_VALUE_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.form.SelectTag.listValue";
    private static final Object EMPTY = new Object();

    @Nullable
    private Object items;

    @Nullable
    private String itemValue;

    @Nullable
    private String itemLabel;

    @Nullable
    private String size;

    @Nullable
    private Object multiple;

    @Nullable
    private TagWriter tagWriter;

    public void setItems(@Nullable Object items) {
        this.items = items != null ? items : EMPTY;
    }

    @Nullable
    protected Object getItems() {
        return this.items;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    @Nullable
    protected String getItemValue() {
        return this.itemValue;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    @Nullable
    protected String getItemLabel() {
        return this.itemLabel;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Nullable
    protected String getSize() {
        return this.size;
    }

    public void setMultiple(Object multiple) {
        this.multiple = multiple;
    }

    @Nullable
    protected Object getMultiple() {
        return this.multiple;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        Object itemsObject;
        tagWriter.startTag("select");
        writeDefaultAttributes(tagWriter);
        if (isMultiple()) {
            tagWriter.writeAttribute("multiple", "multiple");
        }
        tagWriter.writeOptionalAttributeValue(InputTag.SIZE_ATTRIBUTE, getDisplayString(evaluate(InputTag.SIZE_ATTRIBUTE, getSize())));
        Object items = getItems();
        if (items != null) {
            if (items != EMPTY && (itemsObject = evaluate("items", items)) != null) {
                final String selectName = getName();
                String valueProperty = getItemValue() != null ? ObjectUtils.getDisplayString(evaluate("itemValue", getItemValue())) : null;
                String labelProperty = getItemLabel() != null ? ObjectUtils.getDisplayString(evaluate("itemLabel", getItemLabel())) : null;
                OptionWriter optionWriter = new OptionWriter(itemsObject, getBindStatus(), valueProperty, labelProperty, isHtmlEscape()) { // from class: org.springframework.web.servlet.tags.form.SelectTag.1
                    @Override // org.springframework.web.servlet.tags.form.OptionWriter
                    protected String processOptionValue(String resolvedValue) {
                        return SelectTag.this.processFieldValue(selectName, resolvedValue, "option");
                    }
                };
                optionWriter.writeOptions(tagWriter);
            }
            tagWriter.endTag(true);
            writeHiddenTagIfNecessary(tagWriter);
            return 0;
        }
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        this.pageContext.setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, getBindStatus());
        return 1;
    }

    private void writeHiddenTagIfNecessary(TagWriter tagWriter) throws JspException {
        if (isMultiple()) {
            tagWriter.startTag("input");
            tagWriter.writeAttribute("type", "hidden");
            String name = "_" + getName();
            tagWriter.writeAttribute("name", name);
            tagWriter.writeAttribute("value", processFieldValue(name, CustomBooleanEditor.VALUE_1, "hidden"));
            tagWriter.endTag();
        }
    }

    private boolean isMultiple() throws JspException {
        Object multiple = getMultiple();
        if (multiple != null) {
            String stringValue = multiple.toString();
            return "multiple".equalsIgnoreCase(stringValue) || Boolean.parseBoolean(stringValue);
        }
        return forceMultiple();
    }

    private boolean forceMultiple() throws JspException {
        Object editorValue;
        BindStatus bindStatus = getBindStatus();
        Class<?> valueType = bindStatus.getValueType();
        if (valueType != null && typeRequiresMultiple(valueType)) {
            return true;
        }
        if (bindStatus.getEditor() != null && (editorValue = bindStatus.getEditor().getValue()) != null && typeRequiresMultiple(editorValue.getClass())) {
            return true;
        }
        return false;
    }

    private static boolean typeRequiresMultiple(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public int doEndTag() throws JspException {
        if (this.tagWriter != null) {
            this.tagWriter.endTag();
            writeHiddenTagIfNecessary(this.tagWriter);
            return 6;
        }
        return 6;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag, org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        this.tagWriter = null;
        this.pageContext.removeAttribute(LIST_VALUE_PAGE_ATTRIBUTE);
    }
}
