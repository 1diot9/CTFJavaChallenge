package org.springframework.beans;

import org.springframework.core.AttributeAccessorSupport;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanMetadataAttributeAccessor.class */
public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements BeanMetadataElement {

    @Nullable
    private Object source;

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void addMetadataAttribute(BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), attribute);
    }

    @Nullable
    public BeanMetadataAttribute getMetadataAttribute(String name) {
        return (BeanMetadataAttribute) super.getAttribute(name);
    }

    @Override // org.springframework.core.AttributeAccessorSupport, org.springframework.core.AttributeAccessor
    public void setAttribute(String name, @Nullable Object value) {
        super.setAttribute(name, new BeanMetadataAttribute(name, value));
    }

    @Override // org.springframework.core.AttributeAccessorSupport, org.springframework.core.AttributeAccessor
    @Nullable
    public Object getAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.getAttribute(name);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    @Override // org.springframework.core.AttributeAccessorSupport, org.springframework.core.AttributeAccessor
    @Nullable
    public Object removeAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.removeAttribute(name);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }
}
