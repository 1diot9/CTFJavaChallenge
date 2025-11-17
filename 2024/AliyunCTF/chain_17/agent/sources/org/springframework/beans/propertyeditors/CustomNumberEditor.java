package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CustomNumberEditor.class */
public class CustomNumberEditor extends PropertyEditorSupport {
    private final Class<? extends Number> numberClass;

    @Nullable
    private final NumberFormat numberFormat;
    private final boolean allowEmpty;

    public CustomNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty) throws IllegalArgumentException {
        this(numberClass, null, allowEmpty);
    }

    public CustomNumberEditor(Class<? extends Number> numberClass, @Nullable NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {
        if (!Number.class.isAssignableFrom(numberClass)) {
            throw new IllegalArgumentException("Property class must be a subclass of Number");
        }
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            setValue(null);
        } else if (this.numberFormat != null) {
            setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
        } else {
            setValue(NumberUtils.parseNumber(text, this.numberClass));
        }
    }

    public void setValue(@Nullable Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            super.setValue(NumberUtils.convertNumberToTargetClass(num, this.numberClass));
        } else {
            super.setValue(value);
        }
    }

    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            return this.numberFormat.format(value);
        }
        return value.toString();
    }
}
