package org.springframework.beans;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyValue.class */
public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable {
    private final String name;

    @Nullable
    private final Object value;
    private boolean optional;
    private boolean converted;

    @Nullable
    private Object convertedValue;

    @Nullable
    volatile Boolean conversionNecessary;

    @Nullable
    volatile transient Object resolvedTokens;

    public PropertyValue(String name, @Nullable Object value) {
        this.optional = false;
        this.converted = false;
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
    }

    public PropertyValue(PropertyValue original) {
        this.optional = false;
        this.converted = false;
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
        this.optional = original.isOptional();
        this.converted = original.converted;
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original.getSource());
        copyAttributesFrom(original);
    }

    public PropertyValue(PropertyValue original, @Nullable Object newValue) {
        this.optional = false;
        this.converted = false;
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original);
        copyAttributesFrom(original);
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original = this;
        Object source = getSource();
        while (true) {
            Object source2 = source;
            if (!(source2 instanceof PropertyValue)) {
                break;
            }
            PropertyValue pv = (PropertyValue) source2;
            if (source2 == original) {
                break;
            }
            original = pv;
            source = original.getSource();
        }
        return original;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public synchronized boolean isConverted() {
        return this.converted;
    }

    public synchronized void setConvertedValue(@Nullable Object value) {
        this.converted = true;
        this.convertedValue = value;
    }

    @Nullable
    public synchronized Object getConvertedValue() {
        return this.convertedValue;
    }

    @Override // org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof PropertyValue) {
                PropertyValue that = (PropertyValue) other;
                if (!this.name.equals(that.name) || !ObjectUtils.nullSafeEquals(this.value, that.value) || !ObjectUtils.nullSafeEquals(getSource(), that.getSource())) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.AttributeAccessorSupport
    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.name, this.value);
    }

    public String toString() {
        return "bean property '" + this.name + "'";
    }
}
