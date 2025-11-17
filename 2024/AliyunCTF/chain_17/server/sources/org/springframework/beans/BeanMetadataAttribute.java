package org.springframework.beans;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanMetadataAttribute.class */
public class BeanMetadataAttribute implements BeanMetadataElement {
    private final String name;

    @Nullable
    private final Object value;

    @Nullable
    private Object source;

    public BeanMetadataAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BeanMetadataAttribute) {
                BeanMetadataAttribute that = (BeanMetadataAttribute) other;
                if (!this.name.equals(that.name) || !ObjectUtils.nullSafeEquals(this.value, that.value) || !ObjectUtils.nullSafeEquals(this.source, that.source)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.name, this.value);
    }

    public String toString() {
        return "metadata attribute '" + this.name + "'";
    }
}
