package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/AbstractMultiCheckedElementTag.class */
public abstract class AbstractMultiCheckedElementTag extends AbstractCheckedElementTag {
    private static final String SPAN_TAG = "span";

    @Nullable
    private Object items;

    @Nullable
    private String itemValue;

    @Nullable
    private String itemLabel;
    private String element = "span";

    @Nullable
    private String delimiter;

    public void setItems(Object items) {
        Assert.notNull(items, "'items' must not be null");
        this.items = items;
    }

    @Nullable
    protected Object getItems() {
        return this.items;
    }

    public void setItemValue(String itemValue) {
        Assert.hasText(itemValue, "'itemValue' must not be empty");
        this.itemValue = itemValue;
    }

    @Nullable
    protected String getItemValue() {
        return this.itemValue;
    }

    public void setItemLabel(String itemLabel) {
        Assert.hasText(itemLabel, "'itemLabel' must not be empty");
        this.itemLabel = itemLabel;
    }

    @Nullable
    protected String getItemLabel() {
        return this.itemLabel;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Nullable
    public String getDelimiter() {
        return this.delimiter;
    }

    public void setElement(String element) {
        Assert.hasText(element, "'element' cannot be null or blank");
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    public String resolveId() throws JspException {
        Object id = evaluate("id", getId());
        if (id != null) {
            String idString = id.toString();
            if (StringUtils.hasText(idString)) {
                return TagIdGenerator.nextId(idString, this.pageContext);
            }
            return null;
        }
        return autogenerateId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag, org.springframework.web.servlet.tags.form.AbstractFormTag
    public int writeTagContent(TagWriter tagWriter) throws JspException {
        Object items = getItems();
        Object itemsObject = items instanceof String ? evaluate("items", items) : items;
        String itemValue = getItemValue();
        String itemLabel = getItemLabel();
        String valueProperty = itemValue != null ? ObjectUtils.getDisplayString(evaluate("itemValue", itemValue)) : null;
        String labelProperty = itemLabel != null ? ObjectUtils.getDisplayString(evaluate("itemLabel", itemLabel)) : null;
        Class<?> boundType = getBindStatus().getValueType();
        if (itemsObject == null && boundType != null && boundType.isEnum()) {
            itemsObject = boundType.getEnumConstants();
        }
        if (itemsObject == null) {
            throw new IllegalArgumentException("Attribute 'items' is required and must be a Collection, an Array or a Map");
        }
        if (itemsObject.getClass().isArray()) {
            Object[] itemsArray = (Object[]) itemsObject;
            for (int i = 0; i < itemsArray.length; i++) {
                Object item = itemsArray[i];
                writeObjectEntry(tagWriter, valueProperty, labelProperty, item, i);
            }
            return 0;
        }
        if (itemsObject instanceof Collection) {
            Collection<?> optionCollection = (Collection) itemsObject;
            int itemIndex = 0;
            for (Object item2 : optionCollection) {
                writeObjectEntry(tagWriter, valueProperty, labelProperty, item2, itemIndex);
                itemIndex++;
            }
            return 0;
        }
        if (itemsObject instanceof Map) {
            Map<?, ?> optionMap = (Map) itemsObject;
            int itemIndex2 = 0;
            for (Map.Entry<?, ?> entry : optionMap.entrySet()) {
                writeMapEntry(tagWriter, valueProperty, labelProperty, entry, itemIndex2);
                itemIndex2++;
            }
            return 0;
        }
        throw new IllegalArgumentException("Attribute 'items' must be an array, a Collection or a Map");
    }

    private void writeObjectEntry(TagWriter tagWriter, @Nullable String valueProperty, @Nullable String labelProperty, Object item, int itemIndex) throws JspException {
        Object renderValue;
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
        if (valueProperty != null) {
            renderValue = wrapper.getPropertyValue(valueProperty);
        } else if (item instanceof Enum) {
            Enum<?> enumValue = (Enum) item;
            renderValue = enumValue.name();
        } else {
            renderValue = item;
        }
        Object renderLabel = labelProperty != null ? wrapper.getPropertyValue(labelProperty) : item;
        writeElementTag(tagWriter, item, renderValue, renderLabel, itemIndex);
    }

    private void writeMapEntry(TagWriter tagWriter, @Nullable String valueProperty, @Nullable String labelProperty, Map.Entry<?, ?> entry, int itemIndex) throws JspException {
        Object mapKey = entry.getKey();
        Object mapValue = entry.getValue();
        BeanWrapper mapKeyWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapKey);
        BeanWrapper mapValueWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapValue);
        Object renderValue = valueProperty != null ? mapKeyWrapper.getPropertyValue(valueProperty) : mapKey.toString();
        Object renderLabel = labelProperty != null ? mapValueWrapper.getPropertyValue(labelProperty) : mapValue.toString();
        writeElementTag(tagWriter, mapKey, renderValue, renderLabel, itemIndex);
    }

    private void writeElementTag(TagWriter tagWriter, Object item, @Nullable Object value, @Nullable Object label, int itemIndex) throws JspException {
        Object resolvedDelimiter;
        tagWriter.startTag(getElement());
        if (itemIndex > 0 && (resolvedDelimiter = evaluate("delimiter", getDelimiter())) != null) {
            tagWriter.appendValue(resolvedDelimiter.toString());
        }
        tagWriter.startTag("input");
        String id = resolveId();
        Assert.state(id != null, "Attribute 'id' is required");
        writeOptionalAttribute(tagWriter, "id", id);
        writeOptionalAttribute(tagWriter, "name", getName());
        writeOptionalAttributes(tagWriter);
        tagWriter.writeAttribute("type", getInputType());
        renderFromValue(item, value, tagWriter);
        tagWriter.endTag();
        tagWriter.startTag("label");
        tagWriter.writeAttribute("for", id);
        tagWriter.appendValue(convertToDisplayString(label));
        tagWriter.endTag();
        tagWriter.endTag();
    }
}
