package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.BindStatus;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/OptionWriter.class */
class OptionWriter {
    private final Object optionSource;
    private final BindStatus bindStatus;

    @Nullable
    private final String valueProperty;

    @Nullable
    private final String labelProperty;
    private final boolean htmlEscape;

    public OptionWriter(Object optionSource, BindStatus bindStatus, @Nullable String valueProperty, @Nullable String labelProperty, boolean htmlEscape) {
        Assert.notNull(optionSource, "'optionSource' must not be null");
        Assert.notNull(bindStatus, "'bindStatus' must not be null");
        this.optionSource = optionSource;
        this.bindStatus = bindStatus;
        this.valueProperty = valueProperty;
        this.labelProperty = labelProperty;
        this.htmlEscape = htmlEscape;
    }

    public void writeOptions(TagWriter tagWriter) throws JspException {
        if (this.optionSource.getClass().isArray()) {
            renderFromArray(tagWriter);
            return;
        }
        if (this.optionSource instanceof Collection) {
            renderFromCollection(tagWriter);
            return;
        }
        if (this.optionSource instanceof Map) {
            renderFromMap(tagWriter);
            return;
        }
        Object obj = this.optionSource;
        if (obj instanceof Class) {
            Class<?> clazz = (Class) obj;
            if (clazz.isEnum()) {
                renderFromEnum(tagWriter);
                return;
            }
        }
        throw new JspException("Type [" + this.optionSource.getClass().getName() + "] is not valid for option items");
    }

    private void renderFromArray(TagWriter tagWriter) throws JspException {
        doRenderFromCollection(CollectionUtils.arrayToList(this.optionSource), tagWriter);
    }

    private void renderFromMap(TagWriter tagWriter) throws JspException {
        Object obj;
        Object obj2;
        Map<?, ?> optionMap = (Map) this.optionSource;
        for (Map.Entry<?, ?> entry : optionMap.entrySet()) {
            Object mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (this.valueProperty != null) {
                obj = PropertyAccessorFactory.forBeanPropertyAccess(mapKey).getPropertyValue(this.valueProperty);
            } else {
                obj = mapKey;
            }
            Object renderValue = obj;
            if (this.labelProperty != null) {
                obj2 = PropertyAccessorFactory.forBeanPropertyAccess(mapValue).getPropertyValue(this.labelProperty);
            } else {
                obj2 = mapValue;
            }
            Object renderLabel = obj2;
            renderOption(tagWriter, mapKey, renderValue, renderLabel);
        }
    }

    private void renderFromCollection(TagWriter tagWriter) throws JspException {
        doRenderFromCollection((Collection) this.optionSource, tagWriter);
    }

    private void renderFromEnum(TagWriter tagWriter) throws JspException {
        doRenderFromCollection(CollectionUtils.arrayToList(((Class) this.optionSource).getEnumConstants()), tagWriter);
    }

    private void doRenderFromCollection(Collection<?> optionCollection, TagWriter tagWriter) throws JspException {
        Object value;
        for (Object item : optionCollection) {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
            if (this.valueProperty != null) {
                value = wrapper.getPropertyValue(this.valueProperty);
            } else if (item instanceof Enum) {
                Enum<?> enumValue = (Enum) item;
                value = enumValue.name();
            } else {
                value = item;
            }
            Object label = this.labelProperty != null ? wrapper.getPropertyValue(this.labelProperty) : item;
            renderOption(tagWriter, item, value, label);
        }
    }

    private void renderOption(TagWriter tagWriter, Object item, @Nullable Object value, @Nullable Object label) throws JspException {
        tagWriter.startTag("option");
        writeCommonAttributes(tagWriter);
        String valueDisplayString = getDisplayString(value);
        String labelDisplayString = getDisplayString(label);
        tagWriter.writeAttribute("value", processOptionValue(valueDisplayString));
        if (isOptionSelected(value) || (value != item && isOptionSelected(item))) {
            tagWriter.writeAttribute("selected", "selected");
        }
        if (isOptionDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        tagWriter.appendValue(labelDisplayString);
        tagWriter.endTag();
    }

    private String getDisplayString(@Nullable Object value) {
        PropertyEditor editor = value != null ? this.bindStatus.findEditor(value.getClass()) : null;
        return ValueFormatter.getDisplayString(value, editor, this.htmlEscape);
    }

    protected String processOptionValue(String resolvedValue) {
        return resolvedValue;
    }

    private boolean isOptionSelected(@Nullable Object resolvedValue) {
        return SelectedValueComparator.isSelected(this.bindStatus, resolvedValue);
    }

    protected boolean isOptionDisabled() throws JspException {
        return false;
    }

    protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
    }
}
