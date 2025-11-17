package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/ConcurrentModel.class */
public class ConcurrentModel extends ConcurrentHashMap<String, Object> implements Model {
    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model mergeAttributes(@Nullable Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Map attributes) {
        return addAllAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }

    public ConcurrentModel() {
    }

    public ConcurrentModel(String attributeName, Object attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    public ConcurrentModel(Object attributeValue) {
        addAttribute(attributeValue);
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    @Nullable
    public Object put(String key, @Nullable Object value) {
        if (value != null) {
            return super.put((ConcurrentModel) key, (String) value);
        }
        return remove(key);
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends String, ?> map) {
        for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // org.springframework.ui.Model
    public ConcurrentModel addAttribute(String attributeName, @Nullable Object attributeValue) {
        Assert.notNull(attributeName, "Model attribute name must not be null");
        put(attributeName, attributeValue);
        return this;
    }

    @Override // org.springframework.ui.Model
    public ConcurrentModel addAttribute(Object attributeValue) {
        Assert.notNull(attributeValue, "Model attribute value must not be null");
        if (attributeValue instanceof Collection) {
            Collection<?> collection = (Collection) attributeValue;
            if (collection.isEmpty()) {
                return this;
            }
        }
        return addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
    }

    @Override // org.springframework.ui.Model
    public ConcurrentModel addAllAttributes(@Nullable Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (Object attributeValue : attributeValues) {
                addAttribute(attributeValue);
            }
        }
        return this;
    }

    @Override // org.springframework.ui.Model
    public ConcurrentModel addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            putAll(attributes);
        }
        return this;
    }

    @Override // org.springframework.ui.Model
    public ConcurrentModel mergeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (!containsKey(key)) {
                    put(key, value);
                }
            });
        }
        return this;
    }

    @Override // org.springframework.ui.Model
    public boolean containsAttribute(String attributeName) {
        return containsKey(attributeName);
    }

    @Override // org.springframework.ui.Model
    @Nullable
    public Object getAttribute(String attributeName) {
        return get(attributeName);
    }

    @Override // org.springframework.ui.Model
    public Map<String, Object> asMap() {
        return this;
    }
}
