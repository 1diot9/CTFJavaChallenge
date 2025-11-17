package org.springframework.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/AttributeAccessorSupport.class */
public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable {
    private final Map<String, Object> attributes = new LinkedHashMap();

    @Override // org.springframework.core.AttributeAccessor
    public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }

    @Override // org.springframework.core.AttributeAccessor
    @Nullable
    public Object getAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.get(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    public <T> T computeAttribute(String str, Function<String, T> function) {
        Assert.notNull(str, "Name must not be null");
        Assert.notNull(function, "Compute function must not be null");
        T t = (T) this.attributes.computeIfAbsent(str, function);
        Assert.state(t != null, (Supplier<String>) () -> {
            return String.format("Compute function must not return null for attribute named '%s'", str);
        });
        return t;
    }

    @Override // org.springframework.core.AttributeAccessor
    @Nullable
    public Object removeAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.remove(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    public boolean hasAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.containsKey(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    public String[] attributeNames() {
        return StringUtils.toStringArray(this.attributes.keySet());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyAttributesFrom(AttributeAccessor source) {
        Assert.notNull(source, "Source must not be null");
        String[] attributeNames = source.attributeNames();
        for (String attributeName : attributeNames) {
            setAttribute(attributeName, source.getAttribute(attributeName));
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AttributeAccessorSupport) {
                AttributeAccessorSupport that = (AttributeAccessorSupport) other;
                if (this.attributes.equals(that.attributes)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.attributes.hashCode();
    }
}
